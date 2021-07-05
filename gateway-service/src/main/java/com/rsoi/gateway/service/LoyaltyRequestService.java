package com.rsoi.gateway.service;

import com.rsoi.gateway.util.HeadersUtil;
import exceptions.ServiceUnavailableException;
import loyaltyreqres.LoyaltyInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
public class LoyaltyRequestService {
    @Value("${session.loyalty.locale}")
    private String sessionUrl;
    private HeadersUtil headersUtil = new HeadersUtil();
    private String token;

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public ResponseEntity<LoyaltyInfoResponse> getLoylaty(String userUid) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/loyalty/" + userUid;
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<LoyaltyInfoResponse>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.GET,
                        jwtEntity,
                        LoyaltyInfoResponse.class));
    }

    @Recover
    public ResponseEntity<LoyaltyInfoResponse> getLoylaty(ResourceAccessException e, String userUid) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Loyalty Service is unavailbale");
    }

    @Recover
    public ResponseEntity<LoyaltyInfoResponse> getLoylaty(HttpClientErrorException e, String userUid) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return getLoylaty(userUid);
        } else
            throw e;
    }

    @Recover
    public ResponseEntity<LoyaltyInfoResponse> getLoylaty(HttpServerErrorException e, String userUid) {
        throw e;
    }
}
