//package com.mijia.app.resthttp;
//
//import org.apache.http.HttpHost;
//import org.apache.http.conn.socket.ConnectionSocketFactory;
//import org.apache.http.protocol.HttpContext;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//
//import javax.annotation.concurrent.Immutable;
//
//@Immutable
//public class PlainConnectionSocketFactory implements ConnectionSocketFactory {
//
//    public static final PlainConnectionSocketFactory INSTANCE = new PlainConnectionSocketFactory();
//
//    public static PlainConnectionSocketFactory getSocketFactory() {
//        return INSTANCE;
//    }
//
//    public PlainConnectionSocketFactory() {
//        super();
//    }
//
//    @Override
//    public Socket createSocket(final HttpContext context) throws IOException {
//        return new Socket();
//    }
//
//    @Override
//    public Socket connectSocket(
//            final int connectTimeout,
//            final Socket socket,
//            final HttpHost host,
//            final InetSocketAddress remoteAddress,
//            final InetSocketAddress localAddress,
//            final HttpContext context) throws IOException {
//        final Socket sock = socket != null ? socket : createSocket(context);
//        if (localAddress != null) {
//            sock.bind(localAddress);
//        }
//        try {
//            sock.connect(remoteAddress, connectTimeout);
//        } catch (final IOException ex) {
//            try {
//                sock.close();
//            } catch (final IOException ignore) {
//            }
//            throw ex;
//        }
//        return sock;
//    }
//
//}
