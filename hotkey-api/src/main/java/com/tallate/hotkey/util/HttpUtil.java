package com.tallate.hotkey.util;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class HttpUtil {

    private static RestTemplate restTemplate = new RestTemplate();

    /**
     * 查询
     */
    public static <T> T get(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }

    /**
     * 构建请求体
     * 1. Json
     * 2. utf8
     */
    private static HttpEntity<String> buildJsonRequest(String requestJson) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.APPLICATION_JSON_UTF8;
        headers.setContentType(type);
        return new HttpEntity<>(requestJson, headers);
    }

    /**
     * 创建
     */
    public static <T> T post(String url, Object request, Class<T> responseType, Object... uriVariables) {
        return restTemplate.postForObject(url, request, responseType, uriVariables);
    }

    public static <T> T postJson(String url, String requestJson, Class<T> responseType, Object... uriVariables) {
        HttpEntity<String> entity = buildJsonRequest(requestJson);
        return restTemplate.postForObject(url, entity, responseType, uriVariables);
    }

    /**
     * 更新
     */
    public static void put(String url, Object request, Object... uriVariables) {
        restTemplate.put(url, request, uriVariables);
    }

    public static <T> T putJson(String url, String requestJson, Class<T> responseType, Object... uriVariables) {
        HttpEntity<String> entity = buildJsonRequest(requestJson);
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType, uriVariables);
        return responseEntity.getBody();
    }

    /**
     * 删除
     */
    public static void delete(String url, Object... uriVariables) {
        restTemplate.delete(url, uriVariables);
    }

    public static <T> T deleteJson(String url, String requestJson, Class<T> responseType, Object... uriVariables) {
        HttpEntity<String> entity = buildJsonRequest(requestJson);
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, entity, responseType, uriVariables);
        return responseEntity.getBody();
    }

}
