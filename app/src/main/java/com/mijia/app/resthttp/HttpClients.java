//package com.mijia.app.resthttp;
//
//import org.apache.http.annotation.Immutable;
//import org.apache.http.conn.HttpClientConnectionManager;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//
//@Immutable
//public class HttpClients {
//
//    private HttpClients() {
//        super();
//    }
//
//    /**
//     * Creates builder object for construction of custom
//     * {@link CloseableHttpClient} instances.
//     */
//    public static HttpClientBuilder custom() {
//        return HttpClientBuilder.create();
//    }
//
//    /**
//     * Creates {@link CloseableHttpClient} instance with default
//     * configuration.
//     */
//    public static CloseableHttpClient createDefault() {
//        return HttpClientBuilder.create().build();
//    }
//
//    /**
//     * Creates {@link CloseableHttpClient} instance with default
//     * configuration based on ssytem properties.
//     */
//    public static CloseableHttpClient createSystem() {
//        return HttpClientBuilder.create().useSystemProperties().build();
//    }
//
//    /**
//     * Creates {@link CloseableHttpClient} instance that implements
//     * the most basic HTTP protocol support.
//     */
//    public static CloseableHttpClient createMinimal() {
//        return new MinimalHttpClient(new PoolingHttpClientConnectionManager());
//    }
//
//    /**
//     * Creates {@link CloseableHttpClient} instance that implements
//     * the most basic HTTP protocol support.
//     */
//    public static CloseableHttpClient createMinimal(final HttpClientConnectionManager connManager) {
//        return new MinimalHttpClient(connManager);
//    }
//
//
//}
