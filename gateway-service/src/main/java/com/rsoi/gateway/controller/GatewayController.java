package com.rsoi.gateway.controller;

import bookingreqres.BookingInfoResponse;
import bookingreqres.BookingRoomRequest;
import com.rsoi.gateway.service.*;
import exceptions.*;
import hotelreqres.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import loyaltyreqres.LoyaltyInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import reportreqres.ReportBookingResponse;
import reportreqres.ReportFillingResponse;
import sessionreqres.GetUser;
import sessionreqres.NewUser;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@RestController
@CrossOrigin
@RequestMapping(value = "")
public class GatewayController {
    @Autowired
    private SessionRequestService rsSession;
    @Autowired
    private LoyaltyRequestService rsLoyalty;
    @Autowired
    private ReportRequestService rsReport;
    @Autowired
    private HotelRequestService rsHotelReport;
    @Autowired
    private BookingRequestService rsBooking;
    private PublicKey publicKey;

    
    @PostMapping("/auth")
    public ResponseEntity authorize(@RequestHeader(name = "Authorization") String auth) throws UnauthorizedException, ServiceUnavailableException, ForbiddenException {
        ResponseEntity entity = null;
        try {
            if (auth.startsWith("Basic ")) {
                entity = rsSession.authorize(auth);
                HttpHeaders headers = entity.getHeaders();
                String value = headers.getFirst(HttpHeaders.AUTHORIZATION);
                String key = headers.getFirst(HttpHeaders.CONTENT_ENCODING);
                byte[] publicKeyBytes = java.util.Base64.getDecoder().decode(key);
                publicKey =
                        KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                return entity;
            } else {
                throw new UnauthorizedException("No credentials found");
            }
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE) || ex.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                throw new ServiceUnavailableException("SessionService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN))
                throw new ForbiddenException("Forbidden");
            if (ex.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
                throw new UnauthorizedException("Unauthorized");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        throw new ServiceUnavailableException("SessionService is unavailable");
    }

    
    @PostMapping("/verify")
    public void verify(@RequestHeader(name = "Authorization") String auth) throws UnauthorizedException, ServiceUnavailableException, ForbiddenException {
        try {
            if (auth.startsWith("Bearer ")) {
                rsSession.verify(auth);
            } else {
                throw new UnauthorizedException("No credentials found");
            }
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("SessionService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN))
                throw new ForbiddenException("Forbidden");
            if (ex.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                throw ex;
            if (ex.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
                throw new UnauthorizedException("Unauthorized");
        }
    }

    
    @GetMapping("/users")
    public ResponseEntity<Iterable<GetUser>> getUsersAdmin(@RequestHeader(name = "Authorization") String auth) throws ServiceUnavailableException, ForbiddenException {
        try {
            ResponseEntity<Iterable<GetUser>> users = rsSession.getUsersAdmin(auth);
            return ResponseEntity.ok(users.getBody());
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("SessionService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN))
                throw new ForbiddenException("Forbidden");
            if (ex.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                throw ex;
        }
        return ResponseEntity.ok(new ArrayList<>());
    }

    
    @PostMapping("/users")
    public ResponseEntity postUsersAdmin(@RequestHeader(name = "Authorization") String auth,
                                         @RequestBody NewUser user) throws ServiceUnavailableException, ForbiddenException, UserWasAddedException {
        try {
            ResponseEntity entity = rsSession.postUsersAdmin(auth, user);
            return entity;
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("SessionService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN))
                throw new ForbiddenException("Forbidden");
            if (ex.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                throw ex;
            if (ex.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE))
                throw new UserWasAddedException("User with that login already exist. Login: " + user.getLogin());
        }
        return null;
    }


    
    @GetMapping("/loyalty")
    public ResponseEntity<LoyaltyInfoResponse> getLoyalty(@RequestHeader(name = "Authorization") String auth) throws UnauthorizedException, ServiceUnavailableException, ForbiddenException, NotFoundException {
        verify(auth);
        String userUid = getUserUid(auth);
        try {
            if (userUid != null) {
                ResponseEntity<LoyaltyInfoResponse> response = rsLoyalty.getLoylaty(userUid);
                return ResponseEntity.ok(response.getBody());
            } else
                throw new NotFoundException("UserUid not found in token");
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("LoyaltyService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN))
                throw new ForbiddenException("Forbidden");
            if (ex.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                throw ex;
        }
        return ResponseEntity.ok(null);
    }

    
    @GetMapping("/reports/booking")
    public ResponseEntity<Iterable<ReportBookingResponse>> getReportBookings(@RequestHeader(name = "Authorization") String auth,
                                                                             @RequestParam(name = "userUid", required = true) String userUid) throws ForbiddenException, UnauthorizedException, ServiceUnavailableException {
        verify(auth);
        String role = getUserRole(auth);
        if (!role.equals("ADMIN"))
            throw new ForbiddenException("Operation forbidden for this user");
        List<ReportBookingResponse> response = new ArrayList<>();
        try {
            ResponseEntity<Iterable<ReportBookingResponse>> reportBooking = rsReport.getBooking(userUid);
            if (reportBooking.getBody() != null) {
                for (ReportBookingResponse res : reportBooking.getBody()) {
                    ResponseEntity<HotelInfoResponse> hotel = rsHotelReport.getHotel(res.getHotelUid(), null, null, null, null, null);
                    res.setHotelUid(hotel.getBody().getName());
                    response.add(res);
                }
            }
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("ReportsService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN))
                throw new ForbiddenException("Forbidden");
            if (ex.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                throw ex;
        }
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/reports/hotels-filling")
    public ResponseEntity<Iterable<ReportFillingResponse>> getFilling(@RequestHeader(name = "Authorization") String auth) throws ForbiddenException, UnauthorizedException, ServiceUnavailableException, NotFoundException {
        verify(auth);
        String role = getUserRole(auth);
        if (!role.equals("ADMIN"))
            throw new ForbiddenException("Operation forbidden for this user");
        Iterable<ReportFillingResponse> response = new ArrayList<>();
        try {
            ResponseEntity<Iterable<ReportFillingResponse>> reportBooking = rsReport.getReportFilling(Date.from(Instant.now()));
            if (reportBooking.getBody() != null) {
                response = reportBooking.getBody();
                for (ReportFillingResponse res : response) {
                    ResponseEntity<HotelInfoResponse> info = rsHotelReport.getHotel(res.getHotelUid(), null, null, null, null, null);
                    if (info.getBody() != null) {
                        HotelInfoResponse infoRes = info.getBody();
                        res.setRoomsUnavailable(infoRes.getNumberRooms() - res.getRoomsUnavailable());
                    }
                }
            }
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("ReportsService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.FORBIDDEN))
                throw new ForbiddenException("Forbidden");
            if (ex.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                throw ex;
        }
        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/hotels")
    public ResponseEntity<Iterable<HotelInfoResponse>> getHotels(@RequestParam(name = "country", required = false) String country,
                                                                 @RequestParam(name = "city", required = false) String city,
                                                                 @RequestParam(name = "numberGuests", required = false) Integer numberGuests,
                                                                 @RequestParam(name = "dateIn", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date dateIn,
                                                                 @RequestParam(name = "dateOut", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date dateOut) throws ServiceUnavailableException, BadRequestException {
        if (dateIn != null && dateOut != null && dateIn.after(dateOut) || numberGuests != null && numberGuests < 1)
            throw new BadRequestException("Bad format");
        if (dateIn != null && dateOut == null)
            dateOut = dateIn;
        try {
            return rsHotelReport.getHotels(country, city, numberGuests, dateIn, dateOut);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("HotelService is unavailable");
        }
        return ResponseEntity.ok(new ArrayList<>());
    }

    
    @GetMapping("/hotels/{hotelUid}")
    public ResponseEntity<HotelInfoResponse> getHotel(@PathVariable(name = "hotelUid") String hotelUid,
                                                      @RequestParam(name = "country", required = false) String country,
                                                      @RequestParam(name = "city", required = false) String city,
                                                      @RequestParam(name = "numberGuests", required = false) Integer numberGuests,
                                                      @RequestParam(name = "dateIn", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date dateIn,
                                                      @RequestParam(name = "dateOut", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date dateOut) throws ServiceUnavailableException, NotFoundException, BadRequestException {
        if (dateIn != null && dateOut != null && dateIn.after(dateOut) || numberGuests != null && numberGuests < 1)
            throw new BadRequestException("Bad format");
        if (dateIn != null && dateOut == null)
            dateOut = dateIn;
        try {
            return rsHotelReport.getHotel(hotelUid, country, city, numberGuests, dateIn, dateOut);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("HotelService is unavailable");
        }
        return ResponseEntity.ok(null);
    }

    
    @GetMapping("/hotels/{hotelUid}/rooms")
    public ResponseEntity<Iterable<RoomInfoResponse>> getRooms(@PathVariable(name = "hotelUid") String hotelUid,
                                                               @RequestParam(name = "numberGuests", required = false) Integer numberGuests,
                                                               @RequestParam(name = "dateIn")  @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date dateIn,
                                                               @RequestParam(name = "dateOut")  @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date dateOut) throws ServiceUnavailableException, BadRequestException, NotFoundException {
        if (dateIn.after(dateOut) || numberGuests != null && numberGuests < 1)
            throw new BadRequestException("Bad format");
        try {
            return rsHotelReport.getRooms(hotelUid, numberGuests, dateIn, dateOut);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST))
                throw new BadRequestException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("HotelService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(ex.getMessage());
        }
        return ResponseEntity.ok(new ArrayList<>());
    }


    @PatchMapping("/hotels/{hotelUid}/rooms")
    public ResponseEntity patchRooms(@RequestHeader(name = "Authorization") String auth,
                           @PathVariable(name = "hotelUid") String hotelUid,
                           @RequestBody RoomsUpdateRequest request) throws ForbiddenException, UnauthorizedException, ServiceUnavailableException, BadRequestException, NotFoundException {
        verify(auth);
        String userUid = getUserUid(auth);
        String role = getUserRole(auth);
        if (!role.equals("ADMIN"))
            throw new ForbiddenException("Operation forbidden for this user");
        try {
            rsHotelReport.patchHotel(userUid, hotelUid, request);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST))
                throw new BadRequestException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("HotelService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(ex.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    
    @PostMapping("/hotels")
    public ResponseEntity postHotel(@RequestHeader(name = "Authorization") String auth,
                                    @RequestBody HotelAdditionRequest request) throws ForbiddenException, UnauthorizedException, ServiceUnavailableException, BadRequestException {
        verify(auth);
        String role = getUserRole(auth);
        if (!role.equals("ADMIN"))
            throw new ForbiddenException("Operation forbidden for this user");
        try {
            return rsHotelReport.postHotel(request);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST))
                throw new BadRequestException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("HotelService is unavailable");
        }
        return ResponseEntity.ok().build();
    }

    
    @GetMapping("/booking")
    public ResponseEntity<Iterable<BookingInfoResponse>> getBooking(@RequestHeader(name = "Authorization") String auth) throws ForbiddenException, UnauthorizedException, ServiceUnavailableException, NotFoundException {
        verify(auth);
        String userUid = getUserUid(auth);
        if (userUid == null)
            throw new NotFoundException("No user found");
        try {
            return ResponseEntity.ok(rsBooking.getBookings(userUid).getBody());
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("BookingService is unavailable");
        }
        return ResponseEntity.ok(new ArrayList<>());
    }

    
    @GetMapping("/booking/{bookingUid}")
    public ResponseEntity<BookingInfoResponse> getBooking(@RequestHeader(name = "Authorization") String auth,
                                                          @PathVariable(name = "bookingUid") String bookingUid) throws ForbiddenException, UnauthorizedException, ServiceUnavailableException, NotFoundException {
        verify(auth);
        String userUid = getUserUid(auth);
        try {
            return ResponseEntity.ok(rsBooking.getBooking(userUid, bookingUid).getBody());
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("HotelService is unavailable");
        }
        return ResponseEntity.ok(null);
    }

    
    @DeleteMapping("/booking/{bookingUid}/refund")
    public ResponseEntity deleteBooking(@RequestHeader(name = "Authorization") String auth,
                              @PathVariable(name = "bookingUid") String bookingUid) throws ForbiddenException, UnauthorizedException, ServiceUnavailableException, NotFoundException {
        verify(auth);
        try {
            rsBooking.deleteBooking(bookingUid);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("HotelService is unavailable");
        }
        return ResponseEntity.ok().build();
    }

    
    @PostMapping("/booking")
    public ResponseEntity postBooking(@RequestHeader(name = "Authorization") String auth,
                                      @RequestBody BookingRoomRequest request) throws ForbiddenException, UnauthorizedException, ServiceUnavailableException, BadRequestException {
        verify(auth);
        String userUid = getUserUid(auth);
        try {
            return rsBooking.postBooking(userUid, request);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST))
                throw new BadRequestException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("BookingService is unavailable");
        }
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/booking/{bookingUid}/pay")
    public ResponseEntity payBooking(@RequestHeader(name = "Authorization") String auth,
                           @PathVariable(name = "bookingUid") String bookingUid) throws ForbiddenException, UnauthorizedException, ServiceUnavailableException, NotFoundException, NoContentException {
        verify(auth);
        try {
            rsBooking.payBooking(bookingUid);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException(ex.getMessage());
            if (ex.getStatusCode().equals(HttpStatus.NO_CONTENT))
                return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok().build();
    }

    
    @GetMapping("/locations")
    public ResponseEntity<Iterable> getCountryList() throws ServiceUnavailableException {
        Iterable<LocationsResponse> response = new ArrayList<>();
        try {
            response = rsHotelReport.getLocations().getBody();
            if (response == null)
                response = new ArrayList<>();
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException(ex.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    private String getUserUid(String auth) {
        String userUid = null;
        if (publicKey != null) {
            auth = auth.replace("Bearer ", "");
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(auth);
            userUid = (String) claimsJws.getBody().get("userUid");
        }
        return userUid;
    }

    private String getUserRole(String auth) {
        String role = null;
        if (publicKey != null) {
            auth = auth.replace("Bearer ", "");
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(auth);
            role = (String) claimsJws.getBody().get("role");
        }
        return role;
    }
}
