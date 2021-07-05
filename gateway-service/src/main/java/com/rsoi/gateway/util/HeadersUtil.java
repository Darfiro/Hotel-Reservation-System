package com.rsoi.gateway.util;

import listener.CustomRetryListenerSupport;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;
import policy.CustomRetryPolicy;

import java.util.Collections;

public class HeadersUtil {
    public RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory());

    public HeadersUtil() {
    }

    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);
        return factory;
    }

    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        CustomRetryPolicy retryPolicy = new CustomRetryPolicy();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.registerListener(new CustomRetryListenerSupport());

        return retryTemplate;
    }

    public HttpHeaders getBasicAuthHeaders() {
        String adminuserCredentials = "admin:password";
        String encodedCredentials =
                new String(Base64.encodeBase64(adminuserCredentials.getBytes()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + encodedCredentials);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }

    public HttpHeaders getTokenHeaders(String token) {
        HttpHeaders authenticationHeaders = getBasicHeaders();
        authenticationHeaders.set("Authorization", token);
        return authenticationHeaders;
    }

    public String getToken(String currentUrl) {
        HttpEntity<String> httpEntity = new HttpEntity<>(getBasicAuthHeaders());
        String url = currentUrl + "/auth";
        HttpEntity<String> responseEntity = restTemplate.exchange(url,
                HttpMethod.POST, httpEntity, String.class);
        String header = responseEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return header.replace("Bearer ", "");
    }

    public HttpHeaders getBasicHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
