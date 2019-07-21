package com.lhf.websocket.config;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @ClassName: NettyConfig
 * @Description: 存储整个工程的全局配置
 * @Author: liuhefei
 * @Date: 2019/7/21
 * @blog: https://www.imooc.com/u/1323320/articles
 **/
public class NettyConfig {

    /**
     * 存储每一个客户端接入进来时的channel对象
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
