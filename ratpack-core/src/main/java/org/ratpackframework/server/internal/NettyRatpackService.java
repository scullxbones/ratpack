/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ratpackframework.server.internal;

import com.google.common.util.concurrent.AbstractIdleService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NettyRatpackService extends AbstractIdleService implements RatpackService {

  private final Logger logger = Logger.getLogger(getClass().getName());

  private final InetSocketAddress requestedAddress;
  private InetSocketAddress boundAddress;
  private final ChannelInitializer<SocketChannel> channelInitializer;
  private Channel channel;
  private EventLoopGroup group;

  public NettyRatpackService(
    InetSocketAddress requestedAddress,
    ChannelInitializer<SocketChannel> channelInitializer
  ) {
    this.requestedAddress = requestedAddress;
    this.channelInitializer = channelInitializer;
  }

  @Override
  protected void startUp() throws Exception {
    ServerBootstrap bootstrap = new ServerBootstrap();
    group = new NioEventLoopGroup(0, new DefaultThreadFactory("ratpack-group", Thread.MAX_PRIORITY));

    bootstrap.group(group)
      .channel(NioServerSocketChannel.class)
      .childHandler(channelInitializer);

    bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
    bootstrap.option(ChannelOption.SO_REUSEADDR, true);
    bootstrap.option(ChannelOption.SO_BACKLOG, 1024);

    channel = bootstrap.bind(requestedAddress).sync().channel();
    boundAddress = (InetSocketAddress) channel.localAddress();

    if (logger.isLoggable(Level.INFO)) {
      logger.info(String.format("Ratpack started for http://%s:%s", getBindHost(), getBindPort()));
    }
  }

  @Override
  protected void shutDown() throws Exception {
    channel.close().sync();
    group.shutdownGracefully();
  }

  public int getBindPort() {
    return boundAddress == null ? -1 : boundAddress.getPort();
  }

  public String getBindHost() {
    if (boundAddress == null) {
      return null;
    } else {
      return InetSocketAddressBackedBindAddress.determineHost(boundAddress);
    }
  }

}
