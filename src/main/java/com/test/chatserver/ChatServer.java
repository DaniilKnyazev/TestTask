/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.test.chatserver;

import java.net.InetSocketAddress;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.filter.ssl.SslFilter;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 *
 * @author Rooter-than-root
 */
public class ChatServer {
        /** Choose your favorite port number. */
    private static final int PORT = 1100;

    /** Set this to true if you want to make the server SSL */

    public static void main(String[] args) throws Exception {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();

        MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
        chain.addLast("mdc", mdcInjectionFilter);

       

        // Add the compression filter
        //chain.addLast( "Compressor", new CompressionFilter() );

        chain.addLast("codec", new ProtocolCodecFilter(
                new TextLineCodecFactory()));

        addLogger(chain);

        // Bind
        acceptor.setHandler(new ChatProtocolHandler());
        acceptor.bind(new InetSocketAddress(PORT));

        System.out.println("Listening on port " + PORT);
    }

  

    private static void addLogger(DefaultIoFilterChainBuilder chain)
            throws Exception {
        chain.addLast("logger", new LoggingFilter());
        System.out.println("Logging ON");
    }
}
