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
//public class BindPortConnectSocketFactory implements ConnectionSocketFactory {
//
//    public static final BindPortConnectSocketFactory INSTANCE = new BindPortConnectSocketFactory();
//
//    public static BindPortConnectSocketFactory getSocketFactory() {
//        return INSTANCE;
//    }
//
//    public BindPortConnectSocketFactory() {
//        super();
//    }
//
//    @Override
//    public Socket createSocket(HttpContext context) throws IOException {
//        Socket socket = new Socket();
//        socket.bind(new InetSocketAddress(10011));
//        System.out.println("port = [" + socket.getPort() + "]");
//        System.out.println("socket.getLocalPort() = [" + socket.getLocalPort() + "]");
//        return socket;
//    }
//
//    @Override
//    public Socket connectSocket(
//            int connectTimeout,
//            Socket socket,
//            HttpHost host,
//            InetSocketAddress remoteAddress,
//            InetSocketAddress localAddress,
//            HttpContext context) throws IOException {
//        System.out.println("port22 = [" + socket.getPort() + "]");
//        System.out.println("localAddress = [" + localAddress + "]");
//        System.out.println("socket = [" + socket.hashCode() + "]");
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
//}
