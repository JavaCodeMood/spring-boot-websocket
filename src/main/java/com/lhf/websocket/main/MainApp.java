package com.lhf.websocket.main;

import com.lhf.websocket.handler.WebSocketChannelHandler;
import com.lhf.websocket.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @ClassName: MainApp
 * @Description: 程序的入口，负责启动应用
 * @Author: liuhefei
 * @Date: 2019/7/21
 * @blog: https://www.imooc.com/u/1323320/articles
 **/
public class MainApp {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workGroup);
            boot.channel(NioServerSocketChannel.class);
            boot.childHandler(new WebSocketChannelHandler());
            System.out.println("服务端开启等待客户端连接......");
            Channel ch = boot.bind(8888).sync().channel();  //绑定8888端口
            ch.closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //优雅的退出
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
