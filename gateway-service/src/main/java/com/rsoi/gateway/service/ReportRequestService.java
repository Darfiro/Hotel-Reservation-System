package com.rsoi.gateway.service;

import com.rsoi.gateway.util.HeadersUtil;
import exceptions.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
import reportreqres.ReportBookingResponse;
import reportreqres.ReportFillingResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ReportRequestService {
    @Value("${session.report.locale}")
    private String sessionUrl;
    private HeadersUtil headersUtil = new HeadersUtil();
    private String token;

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 100000, resetTimeout = 100000)
    public ResponseEntity<Iterable<ReportBookingResponse>> getBooking(String userUid) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/reports/booking/" + userUid;
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Iterable<ReportBookingResponse>>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.GET,
                        jwtEntity,
                        new ParameterizedTypeReference<Iterable<ReportBookingResponse>>() {
                        }));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 100000, resetTimeout = 30000)
    public ResponseEntity<Iterable<ReportFillingResponse>> getReportFilling(Date date) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = sessionUrl + "/reports/hotel-filling/" + sdf.format(date);
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Iterable<ReportFillingResponse>>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.GET,
                        jwtEntity,
                        new ParameterizedTypeReference<Iterable<ReportFillingResponse>>() {
                        }));
    }

    @Recover
    public ResponseEntity<Iterable<ReportBookingResponse>> getBooking(ResourceAccessException e, String userUid) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Report Service is unavailbale");
    }

    @Recover
    public ResponseEntity<Iterable<ReportBookingResponse>> getBooking(HttpClientErrorException e, String userUid) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return getBooking(userUid);
        } else
            throw e;
    }

    @Recover
    public ResponseEntity<Iterable<ReportBookingResponse>> getBooking(HttpServerErrorException e, String userUid) {
        throw e;
    }

    @Recover
    public ResponseEntity<Iterable<ReportFillingResponse>> getReportFilling(ResourceAccessException e, Date date) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Report Service is unavailbale");
    }

    @Recover
    public ResponseEntity<Iterable<ReportFillingResponse>> getReportFilling(HttpClientErrorException e, Date date) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return getReportFilling(date);
        } else
            throw e;
    }

    @Recover
    public ResponseEntity<Iterable<ReportFillingResponse>> getReportFilling(HttpServerErrorException e, Date date) {
        throw e;
    }
}
