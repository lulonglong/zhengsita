package share.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.Future;

public class IOTest {
    public static void main(String[] args) throws IOException {
        test_nio();
    }

    /**
     * 需要1+n个线程
     * @throws IOException
     */
    public static void test_bio() throws IOException {
        ServerSocket serverSocket = new ServerSocket(9095);
        while (true) {
            System.out.println("等待连接。。");
            final Socket socket = serverSocket.accept(); //阻塞方法
            System.out.println("有客户端连接了。。");
            //加强版BIO，缺点：一个客户端读数据需要一个线程去处理。
            new Thread(() -> {
                try {

                    byte[] bytes = new byte[1024];
                    System.out.println("准备read。。");
                    //接收客户端的数据，阻塞方法，没有数据可读时就阻塞
                    int read = socket.getInputStream().read(bytes);
                    System.out.println("read完毕。。");
                    if (read != -1) {
                        String message = new String(bytes, 0, read);
                        System.out.println("接收到客户端的数据：" + message);
                    }
                    //阻塞方法
                    socket.getOutputStream().write("服务器已收到".getBytes());
                    socket.getOutputStream().flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }


    public static void test_nio() throws IOException {
        //创建ServerSocket通道，并设置为非阻塞，绑定本机端口
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(50111));
        //创建一个选择器Selector,
        Selector selector = Selector.open();
        //注册接收事件，即注册客户端的连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true){

            //此为IO的多路复用机制，linux环境下用的是epoll函数，windows环境下用的是poll(windows系统无epoll)。
            int select = selector.select();


            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                handle(key);
                it.remove();
            }

        }
    }

    private static void handle(SelectionKey key) throws IOException {
        if (key.isAcceptable()){
            System.out.println("有客户端连接事件发生..." );
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(),SelectionKey.OP_READ);
            serverSocketChannel.close();
        }else if (key.isReadable()){
            System.out.println("读取客户端发送的数据:" );
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int length = socketChannel.read(byteBuffer) ;
            if (length != -1){
                System.out.println("服务端发来信息:" + new String(byteBuffer.array(), 0, length,"utf-8"));
            }
            key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        }else if (key.isWritable()){
            System.out.println("服务端写事件..." );
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer writeBuffer = ByteBuffer.wrap("服务器已收到".getBytes("utf-8"));
            socketChannel.write(writeBuffer);
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    /**
     * 0线程阻塞
     * @throws IOException
     */
    public static void test_aio() throws IOException {
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9097));

        // 监听accept事件
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
                try {
                    System.out.println("accept new conn: " + socketChannel.getRemoteAddress());
                    // 再次监听accept事件
                    serverSocketChannel.accept(null, this);
                    // 消息的处理
                    while (true) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        // 将数据读入到buffer中
                        Future<Integer> future = socketChannel.read(buffer);
                        if (future.get() > 0) {
                            buffer.flip();
                            byte[] bytes = new byte[buffer.remaining()];
                            // 将数据读入到byte数组中
                            buffer.get(bytes);

                            String content = new String(bytes, "UTF-8");
                            // 换行符会当成另一条消息传过来
                            if (content.equals("\r\n")) {
                                continue;
                            }
                            if (content.equalsIgnoreCase("quit")) {
                                socketChannel.close();
                                break;
                            } else {
                                System.out.println("receive msg: " + content);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                System.out.println("failed");
            }
        });
        // 阻塞住主线程
        System.in.read();
    }


    public static void zero_copy() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        //  socketChannel.connect(new InetSocketAddress("localhost", 7001));
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9098));
        String fileName = "1.txt";
        //获取文件通道
        FileChannel fileChannel = new FileInputStream(fileName).getChannel();
        //发送开始计时
        Long startTime = System.currentTimeMillis();

        /** transferTo实现零拷贝
         * 无论文件大小
         *在Linux系统下运行transferTo一次性可以传输完成
         * 在windows下一次只能发送8M,超出范围文件需要分段传输
         *  transferTo(long position, long count,WritableByteChannel target)
         *参数1:文件传输时的位置,作为分段传输的标注点
         * 参数2:文件大小
         * 参数3.通道
         */
        long l = fileChannel.transferTo(0, fileChannel.size(), socketChannel);

        System.out.println("发送的字节总数="+l+",耗时:"+(System.currentTimeMillis()-startTime));
        //关闭
        fileChannel.close();
    }

}
