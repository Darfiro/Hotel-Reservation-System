package com.rsoi.booking.service;


import exceptions.ServiceUnavailableException;
import listener.CustomRetryListenerSupport;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import paymentreqres.PaymentAdditionRequest;
import paymentreqres.PaymentInfoResponse;
import policy.CustomRetryPolicy;

import java.util.Collections;
import java.util.Date;

@Service
@EnableRetry(proxyTargetClass=true)
@EnableCircuitBreaker
public class RequestService
{
    private RestTemplate restTemplate = new RestTemplate(simpleClientHttpRequestFactory());
    @Value("${url.payment.locale}")
    private String paymentUrl;
    private String tokenPayment = "";


    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public ResponseEntity<PaymentInfoResponse> getPaymentInfo(String paymentUid)
    {
        if (tokenPayment.equals(""))
            tokenPayment = getToken(paymentUrl);
        String url = paymentUrl + "/payments/" + paymentUid;
        HttpEntity request = new HttpEntity(getTokenHeaders(tokenPayment));

        return retryTemplate().execute((RetryCallback<ResponseEntity<PaymentInfoResponse>,
                ResourceAccessException>) arg0 ->
                restTemplate.exchange(url,
                        HttpMethod.GET,
                        request,
                        PaymentInfoResponse.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public ResponseEntity<String> deletePayment(String paymentUid)
    {
        if (tokenPayment.equals(""))
            tokenPayment = getToken(paymentUrl);
        String url = paymentUrl + "/payments/" + paymentUid;
        HttpEntity request = new HttpEntity(getTokenHeaders(tokenPayment));
        return retryTemplate().execute((RetryCallback<ResponseEntity<String>,
                ResourceAccessException>) arg0 -> restTemplate.exchange(url,
                HttpMethod.DELETE,
                request,
                String.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public ResponseEntity postPayment(PaymentAdditionRequest paymentRequest)
    {
        if (tokenPayment.equals(""))
            tokenPayment = getToken(paymentUrl);
        String url = paymentUrl + "/payments";
        HttpEntity<PaymentAdditionRequest> request = new HttpEntity(paymentRequest, getTokenHeaders(tokenPayment));

        return retryTemplate().execute((RetryCallback<ResponseEntity<Void>,
                ResourceAccessException>) arg0 ->
                restTemplate.exchange(url,
                        HttpMethod.POST,
                        request,
                        void.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public void patchPayment(String paymentUid)
    {
        if (tokenPayment.equals(""))
            tokenPayment = getToken(paymentUrl);
        String url = paymentUrl + "/payments/" + paymentUid;
        HttpEntity request = new HttpEntity(getTokenHeaders(tokenPayment));
        retryTemplate().execute((RetryCallback<ResponseEntity<Void>,
                ResourceAccessException>) arg0 -> restTemplate.exchange(url,
                HttpMethod.PATCH,
                request,
                void.class));
    }


    @Recover()
    public ResponseEntity<PaymentInfoResponse> recoverGetPaymentInfo(ResourceAccessException e,
                                                                                     String paymentUid)
            throws ServiceUnavailableException
    {
        throw new ServiceUnavailableException("BookingService is unavailable.");
    }

    @Recover()
    public ResponseEntity<PaymentInfoResponse> recoverGetPaymentInfo(HttpClientErrorException e,
                                                                                     String paymentUid)
            throws HttpClientErrorException
    {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN))
        {
            tokenPayment = getToken(paymentUrl);
            return getPaymentInfo(paymentUid);
        }
        else
            throw e;
    }

    @Recover()
    public ResponseEntity<PaymentInfoResponse> recoverGetPaymentInfo(HttpServerErrorException e,
                                                                                     String paymentUid)
            throws HttpServerErrorException
    {
        throw e;
    }

    @Recover()
    public   ResponseEntity<String>  recoverDeletePayment(ResourceAccessException e, String paymentUid) throws ServiceUnavailableException
    {
        throw new ServiceUnavailableException("BookingService is unavailable");
    }

    @Recover()
    public   ResponseEntity<String>  recoverDeletePayment(HttpClientErrorException e, String paymentUid) throws HttpClientErrorException
    {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN))
        {
            tokenPayment = getToken(paymentUrl);
            return deletePayment(paymentUid);
        }
        else
            throw e;
    }

    @Recover()
    public   ResponseEntity<String> recoverDeletePayment(HttpServerErrorException e, String paymentUid) throws HttpServerErrorException
    {
        throw e;
    }

    @Recover()
    public  ResponseEntity recoverPostPayment(ResourceAccessException e, String userUid,
                                    PaymentAdditionRequest paymentRequest) throws ServiceUnavailableException
    {
        throw new ServiceUnavailableException("BookingService is unavailable");
    }

    @Recover()
    public ResponseEntity recoverPostPayment(HttpClientErrorException e, String userUid,
                                             PaymentAdditionRequest paymentRequest) throws HttpClientErrorException
    {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN))
        {
            tokenPayment = getToken(paymentUrl);
            return postPayment(paymentRequest);
        }
        else
            throw e;
    }

    @Recover()
    public  ResponseEntity recoverPostPayment(HttpServerErrorException e, String userUid,
                                              PaymentAdditionRequest paymentRequest) throws HttpServerErrorException
    {
        throw e;
    }

    @Recover()
    public void recoverPatchPayment(ResourceAccessException e, String paymentUid) throws ServiceUnavailableException
    {
        throw new ServiceUnavailableException("BookingService is unavailable");
    }

    @Recover()
    public void recoverPatchPayment(HttpClientErrorException e, String paymentUid) throws HttpClientErrorException
    {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN))
        {
            tokenPayment = getToken(paymentUrl);
            patchPayment(paymentUid);
        }
        else
            throw e;
    }

    @Recover()
    public void recoverPatchPayment(HttpServerErrorException e, String paymentUid) throws HttpServerErrorException
    {
        throw e;
    }

    private RetryTemplate retryTemplate()
    {
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        CustomRetryPolicy retryPolicy = new CustomRetryPolicy();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.registerListener(new CustomRetryListenerSupport());

        return retryTemplate;
    }

    private HttpHeaders getBasicAuthHeaders()
    {
        String adminuserCredentials = "admin:password";
        String encodedCredentials =
                new String(Base64.encodeBase64(adminuserCredentials.getBytes()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + encodedCredentials);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }

    private HttpHeaders getTokenHeaders(String token)
    {
        HttpHeaders authenticationHeaders = getBasicHeaders();
        authenticationHeaders.set("Authorization", token);
        return authenticationHeaders;
    }

    private String getToken(String currentUrl)
    {
        HttpEntity<String> httpEntity = new HttpEntity<>(getBasicAuthHeaders());
        String url = currentUrl + "/auth";
        HttpEntity<String> responseEntity = restTemplate.exchange(url,
                HttpMethod.POST, httpEntity, String.class);
        String header = responseEntity.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return header.replace("Bearer ", "");
    }

    private HttpHeaders getBasicHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(5000);
    return factory;
}
}
