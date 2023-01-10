package share.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerInitializer  extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 得到管道
        ChannelPipeline pipeline = ch.pipeline();
        // 添加解码器
        pipeline.addLast("MyHttpCodec",new HttpServerCodec());


        // 添加一个自定义handler
        pipeline.addLast("MyHttpServerHandler",new TestHttpServerHandler());



    }
}
