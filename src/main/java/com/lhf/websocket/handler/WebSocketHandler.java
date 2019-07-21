package com.lhf.websocket.handler;

import com.lhf.websocket.config.NettyConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.util.Date;

/**
 * @ClassName: WebSocketHandler
 * @Description: 接收/处理/响应客户端websocket请求的核心业务处理类
 * @Author: liuhefei
 * @Date: 2019/7/21
 * @blog: https://www.imooc.com/u/1323320/articles
 **/
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;
    private static final String WEB_SOCKET_URL = "ws://localhost:8888/websocket";

    //客户端与服务端创建连接的时候调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        //super.channelActive(ctx);
        NettyConfig.group.add(ctx.channel());  //保存channel
        System.out.println("客户端与服务端连接开启......");
    }

    //客户端与服务器断开连接的时候调用
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        //super.channelInactive(ctx);
        NettyConfig.group.remove(ctx.channel());  //移除channel
        System.out.println("客户端与服务端连接关闭......");
    }

    //服务器接收客户端发送过来的数据结束之后调用
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
        //super.channelReadComplete(ctx);
        ctx.flush();
    }

    //在工程出现异常的时候调用
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        //super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();  //关闭
    }

    //服务端处理客户端websocket请求的核心方法
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        //1. 客户端向服务器发送握手请求
        //2. 建立websocket连接

        //处理客户端向服务器发起http握手请求的业务
        if(msg instanceof FullHttpRequest){
             handHttpRequest(ctx, (FullHttpRequest) msg);
        }else if(msg instanceof WebSocketFrame){  //处理websocket连接业务
            handWebsocketFrame(ctx, (WebSocketFrame) msg);
        }

    }

    /**
     * 处理客户端与服务器之间的websocket业务
     * @param ctx
     * @param frame
     */
    private void handWebsocketFrame(ChannelHandlerContext ctx,  WebSocketFrame frame){
        //判断是否是关闭websocket的指令
        if(frame instanceof CloseWebSocketFrame){
            handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
        }

        //判断是否是ping消息
        if(frame instanceof PingWebSocketFrame){
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        //判断是否是二进制消息，如果是二进制消息，抛出异常，  不支持二进制消息
        if(!(frame instanceof TextWebSocketFrame)){
            System.out.println("目前我们不支持二进制消息");
            throw new RuntimeException("【" + this.getClass().getName() + "】不支持消息");
        }

        //返回应答消息
        //获取客户端向服务器端发送的消息
        String request = ((TextWebSocketFrame) frame).text();
        System.out.println("服务端收到客户端的消息====>>>" + request);
        //创建TextWebSocketFrame对象，接收客户端发送过来的消息
        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString() + ctx.channel().id() + "=====>>>>" + request);

        //群发，服务端向每个连接上来的客户端群发消息
        NettyConfig.group.writeAndFlush(tws);

    }

    /**
     * 处理客户端向服务器发起http握手请求的服务
     * @param ctx
     * @param req
     */
    private void handHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req){
        //如果不成功或不是websocket请求
        if(!req.getDecoderResult().isSuccess()  || !("websocket".equals(req.headers().get("Upgrade")))){
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //创建工厂对象
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WEB_SOCKET_URL, null, false);
        //创建handshaker对象
        handshaker = wsFactory.newHandshaker(req);
        if(handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        }else {
            handshaker.handshake(ctx.channel(), req);
        }


    }

    /**
     * 服务器向客户端响应消息
     * @param ctx
     * @param req
     * @param res
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res){
        //请求失败
        if(res.getStatus().code() != 200){
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        //服务端向客户端发送数据
        ChannelFuture cf = ctx.channel().writeAndFlush(res);
        if(res.getStatus().code() != 200){
            cf.addListener(ChannelFutureListener.CLOSE);  //关闭连接
        }
    }
}
