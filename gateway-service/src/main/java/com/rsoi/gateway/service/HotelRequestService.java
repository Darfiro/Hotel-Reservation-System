package com.rsoi.gateway.service;

import com.rsoi.gateway.util.HeadersUtil;
import exceptions.ServiceUnavailableException;
import hotelreqres.*;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class HotelRequestService {
    @Value("${session.hotel.locale}")
    private String sessionUrl;
    private HeadersUtil headersUtil = new HeadersUtil();
    private String token;

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public ResponseEntity<Iterable<LocationsResponse>> getLocations() {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/hotels/locations";
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Iterable<LocationsResponse>>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.GET,
                        jwtEntity,
                        new ParameterizedTypeReference<Iterable<LocationsResponse>>() {
                        }));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public ResponseEntity<Iterable<HotelInfoResponse>> getHotels(String country, String city, Integer numberGuests, Date dateIn, Date dateOut) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/hotels";
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (dateIn != null)
            builder = builder.queryParam("dateIn", sdf.format(dateIn));
        if (dateOut != null)
            builder = builder.queryParam("dateOut", sdf.format(dateOut));
        if (country != null)
            builder = builder.queryParam("country", country);
        if (city != null)
            builder = builder.queryParam("city", city);
        if (numberGuests != null)
            builder = builder.queryParam("numberGuests", numberGuests);
        UriComponentsBuilder finalBuilder = builder;
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Iterable<HotelInfoResponse>>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(finalBuilder.toUriString(),
                        HttpMethod.GET,
                        jwtEntity,
                        new ParameterizedTypeReference<Iterable<HotelInfoResponse>>() {
                        }));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public ResponseEntity<HotelInfoResponse> getHotel(String hotelUid, String country, String city, Integer numberGuests, Date dateIn, Date dateOut) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/hotels/" + hotelUid;
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (dateIn != null)
            builder = builder.queryParam("dateIn", sdf.format(dateIn));
        if (dateOut != null)
            builder = builder.queryParam("dateOut", sdf.format(dateOut));
        if (country != null)
            builder = builder.queryParam("country", country);
        if (city != null)
            builder = builder.queryParam("city", city);
        if (numberGuests != null)
            builder = builder.queryParam("numberGuests", numberGuests);
        UriComponentsBuilder finalBuilder = builder;
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<HotelInfoResponse>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(finalBuilder.toUriString(),
                        HttpMethod.GET,
                        jwtEntity,
                        HotelInfoResponse.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public ResponseEntity<Iterable<RoomInfoResponse>> getRooms(String hotelUid, Integer numberGuests, Date dateIn, Date dateOut) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/hotels/" + hotelUid + "/rooms";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("numberGuests", numberGuests)
                .queryParam("dateIn", sdf.format(dateIn))
                .queryParam("dateOut", sdf.format(dateOut));
        HttpEntity<String> jwtEntity = new HttpEntity<>(headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Iterable<RoomInfoResponse>>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(builder.toUriString(),
                        HttpMethod.GET,
                        jwtEntity,
                        new ParameterizedTypeReference<Iterable<RoomInfoResponse>>() {
                        }));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public void patchHotel(String userUid, String hotelUid, RoomsUpdateRequest updateRequest) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/hotels/" + userUid + "/" + hotelUid + "/rooms";
        HttpEntity<RoomsUpdateRequest> request = new HttpEntity(updateRequest, headersUtil.getTokenHeaders(token));
        headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity<Void>,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.PATCH,
                        request,
                        void.class));
    }

    @CircuitBreaker(include = {ResourceAccessException.class}, exclude = {HttpClientErrorException.class, HttpServerErrorException.class}, openTimeout = 30000, resetTimeout = 30000)
    public ResponseEntity postHotel(HotelAdditionRequest additionRequest) {
        if (token == null)
            token = headersUtil.getToken(sessionUrl);
        String url = sessionUrl + "/hotels";
        HttpEntity<HotelAdditionRequest> request = new HttpEntity(additionRequest, headersUtil.getTokenHeaders(token));
        return headersUtil.retryTemplate().execute((RetryCallback<ResponseEntity,
                ResourceAccessException>) arg0 ->
                headersUtil.restTemplate.exchange(url,
                        HttpMethod.POST,
                        request,
                        ResponseEntity.class));
    }

    @Recover
    public ResponseEntity<Iterable<HotelInfoResponse>> getHotels(ResourceAccessException e, String country, String city, Integer numberGuests, Date dateIn, Date dateOut) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Hotel Service is unavailbale");
    }

    @Recover
    public ResponseEntity<Iterable<HotelInfoResponse>> getHotels(HttpClientErrorException e, String country, String city, Integer numberGuests, Date dateIn, Date dateOut) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return getHotels(country, city, numberGuests, dateIn, dateOut);
        } else
            throw e;
    }

    @Recover
    public ResponseEntity<Iterable<HotelInfoResponse>> getHotels(HttpServerErrorException e, String country, String city, Integer numberGuests, Date dateIn, Date dateOut) {
        throw e;
    }

    @Recover
    public ResponseEntity<Iterable<LocationsResponse>> getLocations(ResourceAccessException e) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Hotel Service is unavailbale");
    }

    @Recover
    public ResponseEntity<Iterable<LocationsResponse>> getLocations(HttpClientErrorException e) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return getLocations();
        } else
            throw e;
    }

    @Recover
    public ResponseEntity<Iterable<LocationsResponse>> getLocations(HttpServerErrorException e) {
        throw e;
    }

    @Recover
    public ResponseEntity<HotelInfoResponse> getHotel(ResourceAccessException e, String hotelUid, String country, String city, Integer numberGuests, Date dateIn, Date dateOut) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Hotel Service is unavailbale");
    }

    @Recover
    public ResponseEntity<HotelInfoResponse> getHotel(HttpClientErrorException e, String hotelUid, String country, String city, Integer numberGuests, Date dateIn, Date dateOut) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return getHotel(hotelUid, country, city, numberGuests, dateIn, dateOut);
        } else
            throw e;
    }

    @Recover
    public ResponseEntity<HotelInfoResponse> getHotel(HttpServerErrorException e, String hotelUid, String country, String city, Integer numberGuests, Date dateIn, Date dateOut) {
        throw e;
    }

    @Recover
    public ResponseEntity<Iterable<RoomInfoResponse>> getRooms(ResourceAccessException e, String hotelUid, Integer numberGuests, Date dateIn, Date dateOut) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Hotel Service is unavailbale");
    }

    @Recover
    public ResponseEntity<Iterable<RoomInfoResponse>> getRooms(HttpClientErrorException e, String hotelUid, Integer numberGuests, Date dateIn, Date dateOut) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return getRooms(hotelUid, numberGuests, dateIn, dateOut);
        } else
            throw e;
    }

    @Recover
    public ResponseEntity<Iterable<RoomInfoResponse>> getRooms(HttpServerErrorException e, String hotelUid, Integer numberGuests, Date dateIn, Date dateOut) {
        throw e;
    }

    @Recover
    public ResponseEntity postHotel(ResourceAccessException e, HotelAdditionRequest additionRequest) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Hotel Service is unavailbale");
    }

    @Recover
    public ResponseEntity postHotel(HttpClientErrorException e, HotelAdditionRequest additionRequest) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            return postHotel(additionRequest);
        } else
            throw e;
    }

    @Recover
    public ResponseEntity postHotel(HttpServerErrorException e, HotelAdditionRequest additionRequest) {
        throw e;
    }

    @Recover
    public void patchHotel(ResourceAccessException e, String userUid, String hotelUid, RoomsUpdateRequest updateRequest) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Hotel Service is unavailbale");
    }

    @Recover
    public void patchHotel(HttpClientErrorException e, String userUid, String hotelUid, RoomsUpdateRequest updateRequest) {
        if (e.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
            token = headersUtil.getToken(sessionUrl);
            patchHotel(userUid, hotelUid, updateRequest);
        } else
            throw e;
    }

    @Recover
    public void patchHotel(HttpServerErrorException e, String userUid, String hotelUid, RoomsUpdateRequest updateRequest) {
        throw e;
    }
}
