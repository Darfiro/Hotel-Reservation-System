package com.rsoi.gateway.service;

import com.rsoi.gateway.util.HeadersUtil;
import exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import sessionreqres.GetUser;
import sessionreqres.NewUser;

import java.util.Collections;

@Service
public class SessionRequestService {
    @Value("${session.session.locale}")
    private String sessionUrl;
    private HeadersUtil headersUtil = new HeadersUtil();

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public ResponseEntity<Iterable<GetUser>> getUsersAdmin(String token) {
        String url = sessionUrl + "/users";
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Iterable<GetUser>>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.GET,
                        jwtEntity,
                        new ParameterizedTypeReference<Iterable<GetUser>>() {
                        }));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public ResponseEntity postUsersAdmin(String token, NewUser user) {
        String url = sessionUrl + "/users";
        HttpEntity<NewUser> request = new HttpEntity(user, headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Void>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.POST,
                        request,
                        void.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public void verify(String token) {
        String url = sessionUrl + "/verify";
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Void>,
                ResourceAccessException>) arg0 -> headersUtil.restTemplate.exchange(url, HttpMethod.POST, jwtEntity,
                void.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public ResponseEntity authorize(String encodedCredentials) {
        String url = sessionUrl + "/auth";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", encodedCredentials);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> jwtEntity = new HttpEntity<>(httpHeaders);
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity,
                ResourceAccessException>) arg0 -> headersUtil.restTemplate.exchange(url, HttpMethod.POST, jwtEntity,
                ResponseEntity.class));
    }

    @Recover
    public ResponseEntity<Iterable<GetUser>> getUsersAdmin(ResourceAccessException e, String token) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Session Service is unavailbale");
    }

    @Recover
    public ResponseEntity<Iterable<GetUser>> getUsersAdmin(HttpClientErrorException e, String token) {
        throw e;
    }

    @Recover
    public ResponseEntity<Iterable<GetUser>> getUsersAdmin(HttpServerErrorException e, String token) {
        throw e;
    }

    @Recover
    public ResponseEntity postUsersAdmin(ResourceAccessException e, String token, NewUser user) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Session Service is unavailbale");
    }

    @Recover
    public ResponseEntity postUsersAdmin(HttpClientErrorException e, String token, NewUser user) {
        throw e;
    }

    @Recover
    public ResponseEntity postUsersAdmin(HttpServerErrorException e, String token, NewUser user) {
        throw e;
    }

    @Recover()
    public ResponseEntity authorize(ResourceAccessException e, String encodedCredentials)
            throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Session Service is unavailable.");
    }

    @Recover()
    public ResponseEntity authorize(HttpClientErrorException e, String encodedCredentials)
            throws HttpClientErrorException {
        throw e;
    }

    @Recover()
    public ResponseEntity authorize(HttpServerErrorException e, String encodedCredentials)
            throws HttpServerErrorException {
        throw e;
    }

    @Recover()
    public void verify(ResourceAccessException e, String token)
            throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Session Service is unavailable.");
    }

    @Recover()
    public void verify(HttpClientErrorException e, String token)
            throws HttpClientErrorException {
        throw e;
    }

    @Recover()
    public void verify(HttpServerErrorException e, String token)
            throws HttpServerErrorException {
        throw e;
    }


}
