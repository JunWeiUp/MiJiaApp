//package com.mijia.app.resthttp;
//
//import android.text.TextUtils;
//
//import org.apache.http.client.HttpClient;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
//import org.springframework.web.client.RestTemplate;
//
//public class RestTemplateUtil2 {
//
//    public static RestTemplate restTemplate;
//
//    public static RestTemplate httpsRestTemplate;
//
//    private static int timeout = 5000;
//
//    private static String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";
//
//    private static String APPLICATION_XML_UTF8 = "application/xml;charset=UTF-8";
//
//    public RestTemplateUtil2() throws Exception {
//        restTemplate = new RestTemplate();
//        HttpClient httpClient = BindPortHttpClientBuilder.create().build();
//        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
//
//        factory.setReadTimeout(timeout);
//        factory.setConnectTimeout(timeout);
//        restTemplate.setRequestFactory(factory);
//
//    }
//
//    private static String[] split(final String s) {
////        if (TextUtils.isBlank(s)) {
////            return null;
////        }
//        return s.split(" *, *");
//    }
//
//    private <T> T post(String url, String param, String mediaType, Class<T> bodyType) {
//
//        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType(mediaType);
//        headers.setContentType(type);
//
//        return exchange(url, param, HttpMethod.POST, bodyType, headers);
//    }
//
//    public <T> T postJson(String url, String param, Class<T> bodyType) {
//        return post(url, param, APPLICATION_JSON_UTF8, bodyType);
//    }
//
//    public <T> T postXml(String url, String param, Class<T> bodyType) {
//        return post(url, param, APPLICATION_XML_UTF8, bodyType);
//    }
//
//    public <T> T get(String url, String param, Class<T> bodyType) {
//
//        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType(APPLICATION_JSON_UTF8);
//        headers.setContentType(type);
//
//        return exchange(url, param, HttpMethod.GET, bodyType, headers);
//
//    }
//
//    public <T> T put(String url, String data, Class<T> bodyType) {
//
//        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType(APPLICATION_JSON_UTF8);
//        headers.setContentType(type);
//
//        return exchange(url, data, HttpMethod.PUT, bodyType, headers);
//    }
//
//    public <T> T delete(String url, String data, Class<T> bodyType) {
//
//        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType(APPLICATION_JSON_UTF8);
//        headers.setContentType(type);
//
//        return exchange(url, data, HttpMethod.DELETE, bodyType, headers);
//    }
//
//    /**
//     * 发送/获取 服务端数据(主要用于解决发送put,delete方法无返回值问题)
//     *
//     * @param url
//     * @param content
//     * @param method
//     * @param bodyType
//     * @param headers
//     * @param <T>
//     * @return
//     */
//    private  <T> T exchange(String url, String content, HttpMethod method, Class<T> bodyType, HttpHeaders headers) {
//
//        // 发送请求
//        HttpEntity<String> entity = new HttpEntity<>(content, headers);
//        ResponseEntity<T> resultEntity = restTemplate.exchange(url, method, entity, bodyType);
//
//        return resultEntity.getBody();
//    }
//
//    public <T> T httpsGet(String url, String param, Class<T> bodyType) {
//
//        HttpHeaders headers = new HttpHeaders();
//        MediaType type = MediaType.parseMediaType(APPLICATION_JSON_UTF8);
//        headers.setContentType(type);
//
//        return exchangeHttps(url, param, HttpMethod.GET, bodyType, headers);
//
//    }
//
//    private <T> T exchangeHttps(String url, String content, HttpMethod method, Class<T> bodyType, HttpHeaders headers) {
//
//        // 发送请求
//        HttpEntity<String> entity = new HttpEntity<>(content, headers);
//        ResponseEntity<T> resultEntity = httpsRestTemplate.exchange(url, method, entity, bodyType);
//
//        return resultEntity.getBody();
//    }
//
//    public static void main(String[] args){
//        RestTemplateUtil2 restTemplateUtil2 = null;
//        try {
//            restTemplateUtil2 = new RestTemplateUtil2();
//            String url = "http://www.baidu.com";
////            String s = restTemplateUtil2.get(url,null,String.class);
//            for (int i = 1 ; i <= 100;i++) {
//                String s = restTemplateUtil2.httpsGet(url, null, String.class);
//                System.out.println("s = [" + s + "]");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("err = [" + e + "]");
//        }
//
//    }
//
//
//}
