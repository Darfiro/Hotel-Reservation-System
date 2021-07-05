package com.rsoi.gateway.service;

import bookingreqres.BookingInfoResponse;
import bookingreqres.BookingRoomRequest;
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

@Service
public class BookingRequestService {
    @Value("${session.booking.locale}")
    private String sessionUrl;
    private HeadersUtil headersUtil = new HeadersUtil();
    private String token;

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public ResponseEntity<Iterable<BookingInfoResponse>> getBookings(String userUid) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/booking/" + userUid;
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Iterable<BookingInfoResponse>>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.GET,
                        jwtEntity,
                        new ParameterizedTypeReference<Iterable<BookingInfoResponse>>() {
                        }));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public ResponseEntity<BookingInfoResponse> getBooking(String userUid, String bookingUid) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/booking/" + userUid + "/" + bookingUid;
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<BookingInfoResponse>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.GET,
                        jwtEntity,
                        BookingInfoResponse.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public void deleteBooking(String bookingUid) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/booking/" + bookingUid + "/refund";
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Void>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.DELETE,
                        jwtEntity,
                        void.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public ResponseEntity postBooking(String userUid, BookingRoomRequest roomRequest) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/booking/" + userUid;
        HttpEntity<BookingRoomRequest> request = new HttpEntity(roomRequest, headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.POST,
                        request,
                        ResponseEntity.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public void payBooking(String bookingUid) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/booking/" + bookingUid + "/pay";
        HttpEntity<String> request = new HttpEntity(headersUtil.getTokenHeaders(token));
        headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Void>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.PATCH,
                        request,
                        void.class));
    }

    @Recover
    public ResponseEntity<Iterable<BookingInfoResponse>> getBookings(ResourceAccessException e, String userUid) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Booking Service is unavailbale");
    }

    @Recover
    public ResponseEntity<Iterable<BookingInfoResponse>> getBookings(HttpClientErrorException e, String userUid) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return getBookings(userUid);
        } else
            throw e;
    }

    @Recover
    public ResponseEntity<Iterable<BookingInfoResponse>> getBookings(HttpServerErrorException e, String userUid) {
        throw e;
    }

    @Recover
    public ResponseEntity<BookingInfoResponse> getBooking(ResourceAccessException e, String userUid, String bookingUid) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Booking Service is unavailbale");
    }

    @Recover
    public ResponseEntity<BookingInfoResponse> getBooking(HttpClientErrorException e, String userUid, String bookingUid) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return getBooking(userUid, bookingUid);
        } else
            throw e;
    }

    @Recover
    public ResponseEntity<BookingInfoResponse> getBooking(HttpServerErrorException e, String userUid, String bookingUid) {
        throw e;
    }

    @Recover
    public void deleteBooking(ResourceAccessException e, String bookingUid) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Booking Service is unavailbale");
    }

    @Recover
    public void deleteBooking(HttpClientErrorException e, String bookingUid) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            deleteBooking(bookingUid);
        } else
            throw e;
    }

    @Recover
    public void deleteBooking(HttpServerErrorException e, String bookingUid) {
        throw e;
    }


    @Recover
    public ResponseEntity postBooking(ResourceAccessException e, String userUid, BookingRoomRequest roomRequest) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Booking Service is unavailbale");
    }

    @Recover
    public ResponseEntity postBooking(HttpClientErrorException e, String userUid, BookingRoomRequest roomRequest) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return postBooking(userUid, roomRequest);
        } else
            throw e;
    }

    @Recover
    public ResponseEntity postBooking(HttpServerErrorException e, String userUid, BookingRoomRequest roomRequest) {
        throw e;
    }

    @Recover
    public void payBooking(ResourceAccessException e, String bookingUid) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Booking Service is unavailbale");
    }

    @Recover
    public void payBooking(HttpClientErrorException e, String bookingUid) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            payBooking(bookingUid);
        } else
            throw e;
    }

    @Recover
    public void payBooking(HttpServerErrorException e, String bookingUid) {
        throw e;
    }

}
