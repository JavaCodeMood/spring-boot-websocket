package com.lhf.websocket.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;


/**
 * @ClassName: WebSocketChannelHandler
 * @Description: 初始化连接时候的各个组件
 * @Author: liuhefei
 * @Date: 2019/7/21
 * @blog: https://www.imooc.com/u/1323320/articles
 **/
public class WebSocketChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("http-codec", new HttpServerCodec());
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        ch.pipeline().addLast("handler", new WebSocketHandler());
    }
}
