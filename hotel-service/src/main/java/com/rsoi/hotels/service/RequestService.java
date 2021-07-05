package com.rsoi.hotels.service;

import bookingreqres.BookingRoomBookedResponse;
import bookingreqres.BookingRoomRequest;
import bookingreqres.BookingRoomUpdateResponse;
import exceptions.ServiceUnavailableException;
import listener.CustomRetryListenerSupport;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
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
import org.springframework.web.util.UriComponentsBuilder;
import policy.CustomRetryPolicy;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;

@Service
@EnableRetry(proxyTargetClass=true)
@EnableCircuitBreaker
public class RequestService
{
    private RestTemplate restTemplate = new RestTemplate();
    @Value("${url.booking.locale}")
    private String bookingUrl;
    private String tokenBooking = "";

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public ResponseEntity<Iterable<BookingRoomUpdateResponse>> getBookingInfo(String hotelUid,
                                                                              Integer roomNumber,
                                                                              Date dateIn,
                                                                              Date dateOut)
    {
        if (tokenBooking.equals(""))
            tokenBooking = getToken(bookingUrl);
        String url = bookingUrl + "/booking/"+hotelUid+"/updateInfo";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("roomNumber", roomNumber)
                .queryParam("dateIn", sdf.format(dateIn))
                .queryParam("dateOut", sdf.format(dateOut));
        HttpEntity request = new HttpEntity(getTokenHeaders(tokenBooking));

        return retryTemplate().execute((RetryCallback<ResponseEntity<Iterable<BookingRoomUpdateResponse>>,
                        ResourceAccessException>) arg0 ->
                        restTemplate.exchange(builder.toUriString(),
                        HttpMethod.GET,
                        request,
                        new ParameterizedTypeReference<Iterable<BookingRoomUpdateResponse>>(){},
                        hotelUid));
    }

    @CircuitBreaker(include = {ResourceAccessException.class, HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public ResponseEntity<BookingRoomBookedResponse> getRoomBooked(String hotelUid,
                                                                  Date dateIn,
                                                                  Date dateOut)
    {
        if (tokenBooking.equals(""))
            tokenBooking = getToken(bookingUrl);
        String url = bookingUrl + "/booking/"+hotelUid + "/booked";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("dateIn", sdf.format(dateIn))
                .queryParam("dateOut", sdf.format(dateOut));
        HttpEntity request = new HttpEntity(getTokenHeaders(tokenBooking));

        return retryTemplate().execute((RetryCallback<ResponseEntity<BookingRoomBookedResponse>,
                ResourceAccessException>) arg0 ->
                restTemplate.exchange(builder.toUriString(),
                        HttpMethod.GET,
                        request,
                        BookingRoomBookedResponse.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public void deleteBooking(String bookingUid)
    {
        if (tokenBooking.equals(""))
            tokenBooking = getToken(bookingUrl);
        String url = bookingUrl + "/booking/" + bookingUid + "/refund";
        HttpEntity request = new HttpEntity(getTokenHeaders(tokenBooking));
        retryTemplate().execute((RetryCallback<ResponseEntity<Void>,
                ResourceAccessException>) arg0 -> restTemplate.exchange(url,
                HttpMethod.DELETE,
                request,
                void.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 10000, resetTimeout = 30000)
    public void postBooking(String userUid,
                             BookingRoomRequest bookingRequest)
    {
        if (tokenBooking.equals(""))
            tokenBooking = getToken(bookingUrl);
        String url = bookingUrl + "/booking/" + userUid;
        HttpEntity<BookingRoomRequest> request = new HttpEntity(bookingRequest, getTokenHeaders(tokenBooking));

        retryTemplate().execute((RetryCallback<ResponseEntity<Void>,
                ResourceAccessException>) arg0 ->
                restTemplate.exchange(url,
                        HttpMethod.POST,
                        request,
                        void.class));
    }



    @Recover()
    public ResponseEntity<Iterable<BookingRoomUpdateResponse>> recoverGetBookingInfo(ResourceAccessException e,
                                                             String hotelUid,
                                                                              Integer roomNumber,
                                                                              Date dateIn,
                                                                              Date dateOut)
            throws ServiceUnavailableException
    {
        throw new ServiceUnavailableException("BookingService is unavailable.");
    }

    @Recover()
    public ResponseEntity<Iterable<BookingRoomUpdateResponse>> recoverGetBookingInfo(HttpClientErrorException e,
                                                             String hotelUid,
                                                                              Integer roomNumber,
                                                                              Date dateIn,
                                                                              Date dateOut)
            throws HttpClientErrorException
    {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN))
        {
            tokenBooking = getToken(bookingUrl);
            return getBookingInfo(hotelUid, roomNumber, dateIn, dateOut);
        }
        else
            throw e;
    }

    @Recover()
    public ResponseEntity<Iterable<BookingRoomUpdateResponse>> recoverGetBookingInfo(HttpServerErrorException e,
                                                             String hotelUid,
                                                                              Integer roomNumber,
                                                                              Date dateIn,
                                                                              Date dateOut)
            throws HttpServerErrorException
    {
        throw e;
    }

    @Recover()
    public ResponseEntity<BookingRoomBookedResponse> recoverGetRoomBooked(ResourceAccessException e,
                                                                                    String hotelUid,
                                                                                    Date dateIn,
                                                                                    Date dateOut)
            throws ServiceUnavailableException
    {
        throw new ServiceUnavailableException("BookingService is unavailable.");
    }

    @Recover()
    public ResponseEntity<BookingRoomBookedResponse> recoverGetRoomBooked(HttpClientErrorException e,
                                                                                     String hotelUid,
                                                                                     Date dateIn,
                                                                                     Date dateOut)
            throws HttpClientErrorException
    {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN))
        {
            tokenBooking = getToken(bookingUrl);
            return getRoomBooked(hotelUid, dateIn, dateOut);
        }
        else
            throw e;
    }

    @Recover()
    public ResponseEntity<BookingRoomBookedResponse> recoverGetRoomBooked(HttpServerErrorException e,
                                                                                     String hotelUid,
                                                                                     Date dateIn,
                                                                                     Date dateOut)
            throws HttpServerErrorException
    {
        throw e;
    }

    @Recover()
    public  void recoverPostBooking(ResourceAccessException e, String userUid,
                                    BookingRoomRequest bookingRequest) throws ServiceUnavailableException
    {
        throw new ServiceUnavailableException("BookingService is unavailable");
    }

    @Recover()
    public  void recoverPostBooking(HttpClientErrorException e, String userUid,
                                    BookingRoomRequest bookingRequest) throws HttpClientErrorException
    {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN))
        {
            tokenBooking = getToken(bookingUrl);
            postBooking(userUid, bookingRequest);
        }
        else
            throw e;
    }

    @Recover()
    public  void recoverPostBooking(HttpServerErrorException e, String userUid,
                                    BookingRoomRequest bookingRequest) throws HttpServerErrorException
    {
        throw e;
    }

    @Recover()
    public  void recoverDeleteBooking(ResourceAccessException e, String bookingUid) throws ServiceUnavailableException
    {
        throw new ServiceUnavailableException("BookingService is unavailable");
    }

    @Recover()
    public  void recoverDeleteBooking(HttpClientErrorException e, String bookingUid) throws HttpClientErrorException
    {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN))
        {
            tokenBooking = getToken(bookingUrl);
            deleteBooking(bookingUid);
        }
        else
            throw e;
    }

    @Recover()
    public  void recoverDeleteBooking(HttpServerErrorException e, String bookingUid) throws HttpServerErrorException
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
}
