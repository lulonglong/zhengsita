package share.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    //读取客户端的数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        //判断msg是不是HttpObject请求

        if(msg instanceof HttpRequest){

            //拦截ico请求
            String uri = ((HttpRequest) msg).uri();
            if("/favicon.ico".equals(uri)){
                System.out.println("请求了favicon.ico，不做响应");
                return;
            }

            System.out.println("msg类型=" + msg.getClass());
            System.out.println("客户端地址；" + ctx.channel().remoteAddress());
            //回复信息给浏览器【Http协议】
            ByteBuf content = Unpooled.copiedBuffer("hello,我是服务器", CharsetUtil.UTF_8);
            //构造一个Http的响应，即Httpresponse
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,content);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");

            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            //将构建好的reaponse返回
            ctx.writeAndFlush(response);
        }
    }
}
