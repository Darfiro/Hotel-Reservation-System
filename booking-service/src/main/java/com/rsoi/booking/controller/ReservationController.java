package com.rsoi.booking.controller;

import bookingreqres.BookingInfoResponse;
import bookingreqres.BookingRoomBookedResponse;
import bookingreqres.BookingRoomRequest;
import bookingreqres.BookingRoomUpdateResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.rsoi.booking.messaging.QueueProducer;
import com.rsoi.booking.model.Reservation;
import com.rsoi.booking.service.DatabaseService;
import com.rsoi.booking.service.RequestService;
import exceptions.*;
import loyaltyreqres.LoyaltyUpdateRequest;
import model.BookingInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import paymentreqres.PaymentAdditionRequest;
import paymentreqres.PaymentInfoResponse;
import paymentreqres.PaymentUpdate;
import reportreqres.ReportAdditionReqRes;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/booking")
public class ReservationController {
    @Autowired
    private DatabaseService db;
    @Autowired
    private RequestService rs;
    @Autowired
    private QueueProducer queueProducer;

    @GetMapping("/{userUid}")
    public ResponseEntity<Iterable<BookingInfoResponse>> getBookings(@PathVariable(name = "userUid") String userUid) {
        Iterable<Reservation> bookings = db.getReservations(userUid);
        List<BookingInfoResponse> response = new ArrayList<>();
        for (Reservation reservation : bookings) {
            BookingInfoResponse res = new BookingInfoResponse();
            res.setHotelUid(reservation.getHotelUid().toString());
            BookingInterval interval = new BookingInterval();
            interval.setDateIn(reservation.getDateIn());
            interval.setDateOut(reservation.getDateOut());
            res.setBookingInterval(interval);
            res.setBookingUid(reservation.getBookingUid().toString());
            res.setRoomNumber(reservation.getRoomNumber());
            try {
                ResponseEntity<PaymentInfoResponse> responseResponseEntity = rs.getPaymentInfo(reservation.getPaymentUid().toString());
                if (responseResponseEntity.getBody() != null) {
                    PaymentInfoResponse paymentInfoResponse = responseResponseEntity.getBody();
                    res.setPrice(paymentInfoResponse.getPrice());
                    res.setStatus(paymentInfoResponse.getStatus());
                }
            } catch (HttpStatusCodeException ex) {
                if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                    System.out.println("PaymentService is unavailable " + reservation.getPaymentUid());
                if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                    System.out.println("PaymentService: payment is not found " + reservation.getPaymentUid());
            }
            response.add(res);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userUid}/{bookingUid}")
    public ResponseEntity<BookingInfoResponse> getBooking(@PathVariable(name = "userUid") String userUid,
                                                          @PathVariable(name = "bookingUid") String bookingUid) {
        Reservation reservation = db.getReservation(bookingUid);
        BookingInfoResponse res = new BookingInfoResponse();
        res.setHotelUid(reservation.getHotelUid().toString());
        BookingInterval interval = new BookingInterval();
        interval.setDateIn(reservation.getDateIn());
        interval.setDateOut(reservation.getDateOut());
        res.setBookingInterval(interval);
        res.setBookingUid(reservation.getBookingUid().toString());
        res.setRoomNumber(reservation.getRoomNumber());
        try {
            ResponseEntity<PaymentInfoResponse> responseResponseEntity = rs.getPaymentInfo(reservation.getPaymentUid().toString());
            if (responseResponseEntity.getBody() != null) {
                PaymentInfoResponse paymentInfoResponse = responseResponseEntity.getBody();
                res.setPrice(paymentInfoResponse.getPrice());
                res.setStatus(paymentInfoResponse.getStatus());
            }
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                System.out.println("PaymentService is unavailable " + reservation.getPaymentUid());
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                System.out.println("PaymentService: payment is not found " + reservation.getPaymentUid());
        }
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{bookingUid}/refund")
    public void deleteBooking(@PathVariable(name = "bookingUid") String bookingUid) throws NotFoundException, NoContentException, ServiceUnavailableException, InternalServerErrorException {
        Reservation reservation = db.getReservation(bookingUid);
        try {
            if (reservation == null)
                throw new NotFoundException("BookingService: booking " + bookingUid + " not found");
            ResponseEntity<String> response = rs.deletePayment(reservation.getPaymentUid().toString());
            if (response.getBody() != null) {
                String status = response.getBody();
                constructReport(reservation, status);
            }
        } catch (HttpStatusCodeException ex) {

            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                constructPaymentUpdate(reservation.getPaymentUid().toString(), false);
            }
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException("PaymentService: payment not found for booking " + bookingUid);
            if (ex.getStatusCode().equals(HttpStatus.NO_CONTENT))
                throw new NoContentException("Canceled booking " + bookingUid);
        }
    }

    @GetMapping("/{hotelUid}/updateInfo")
    public ResponseEntity<Iterable<BookingRoomUpdateResponse>> getBookingForUpdate(@PathVariable(name = "hotelUid") String hotelUid,
                                                                                   @RequestParam(name = "roomNumber") Integer roomNumber,
                                                                                   @RequestParam(name = "dateIn") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateIn,
                                                                                   @RequestParam(name = "dateOut") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOut) throws BadRequestException, ServiceUnavailableException, NotFoundException {
        if (roomNumber == null || dateIn == null || dateOut == null)
            throw new BadRequestException("Not all parameters are set");
        Iterable<Reservation> reservations = db.getReservationsByParams(hotelUid, roomNumber, dateIn, dateOut);
        List<BookingRoomUpdateResponse> response = new ArrayList<>();
        for (Reservation reservation : reservations) {
            BookingRoomUpdateResponse info = new BookingRoomUpdateResponse();
            info.setBookingUid(reservation.getBookingUid().toString());
            try {
                ResponseEntity<PaymentInfoResponse> responseResponseEntity = rs.getPaymentInfo(reservation.getPaymentUid().toString());
                if (responseResponseEntity.getBody() != null) {
                    PaymentInfoResponse paymentInfoResponse = responseResponseEntity.getBody();
                    info.setStatus(paymentInfoResponse.getStatus());
                    response.add(info);
                }
            } catch (HttpStatusCodeException ex) {
                if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                    throw new ServiceUnavailableException("PaymentService is unavailable");
                if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                    throw new NotFoundException("PaymentService: payment not found for booking " + reservation.getBookingUid());
            }
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hotelUid}/booked")
    public ResponseEntity<BookingRoomBookedResponse> getBookingForRooms(@PathVariable(name = "hotelUid") String hotelUid,
                                                                        @RequestParam(name = "dateIn") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateIn,
                                                                        @RequestParam(name = "dateOut") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOut) throws BadRequestException, ServiceUnavailableException, NotFoundException {
        if (dateIn == null || dateOut == null)
            throw new BadRequestException("Not all parameters are set");
        Iterable<Reservation> reservations = db.getReservationsByParams(hotelUid, dateIn, dateOut);
        BookingRoomBookedResponse response = new BookingRoomBookedResponse();
        ;
        for (Reservation reservation : reservations) {
            try {
                ResponseEntity<PaymentInfoResponse> responseResponseEntity = rs.getPaymentInfo(reservation.getPaymentUid().toString());
                if (responseResponseEntity.getBody() != null) {
                    PaymentInfoResponse paymentInfoResponse = responseResponseEntity.getBody();
                    if (!paymentInfoResponse.getStatus().equals("Cancelled") || !paymentInfoResponse.getStatus().equals("Reversed")) {
                        response.addRoom(reservation.getRoomNumber());
                    }
                }
            } catch (HttpStatusCodeException ex) {
                if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                    throw new ServiceUnavailableException("PaymentService is unavailable");
                if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                    throw new NotFoundException("PaymentService: payment not found for booking " + reservation.getBookingUid());
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userUid}")
    public ResponseEntity postBooking(@PathVariable(name = "userUid") String userUid,
                                      @RequestBody BookingRoomRequest request) throws BadRequestException, ServiceUnavailableException, InternalServerErrorException {
        if (request.getHotelUid() == null || request.getBookingInterval() == null || request.getRoomNumber() == null ||
                request.getPrice() == null || request.getBookingInterval().getDateIn() == null || request.getBookingInterval().getDateOut() == null)
            throw new BadRequestException("Not all parameters set");
        try {
            PaymentAdditionRequest paymentAdditionRequest = new PaymentAdditionRequest();
            paymentAdditionRequest.setPrice(request.getPrice());
            ResponseEntity responseEntity = rs.postPayment(paymentAdditionRequest);
            if (responseEntity != null) {
                String location = responseEntity.getHeaders().getLocation().getPath();
                String paymentUid = location.substring(location.lastIndexOf('/') + 1);
                Reservation reservation = db.saveReservation(userUid, request, paymentUid);
                URI newLocation = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(reservation.getPaymentUid()).toUri();
                LoyaltyUpdateRequest loyaltyUpdateRequest = new LoyaltyUpdateRequest();
                loyaltyUpdateRequest.setUserUid(reservation.getUserUid().toString());
                queueProducer.produce(loyaltyUpdateRequest);
                constructReport(reservation, "New");
                return ResponseEntity.created(newLocation).build();
            }
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("PaymentService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST))
                throw new BadRequestException("PaymentService: bad request");
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException("Internal server error: JsonProcessingException " + e.getMessage());
        }
        return null;
    }

    @PatchMapping("/{bookingUid}/pay")
    public void payBooking(@PathVariable(name = "bookingUid") String bookingUid) throws NotFoundException, InternalServerErrorException, ServiceUnavailableException, NoContentException {
        Reservation reservation = db.getReservation(bookingUid);
        try {
            if (reservation == null)
                throw new NotFoundException("BookingService: booking " + bookingUid + " not found");
            rs.patchPayment(reservation.getPaymentUid().toString());
            constructReport(reservation, "Paid");
        } catch (HttpStatusCodeException ex) {

            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                constructPaymentUpdate(reservation.getPaymentUid().toString(), true);
            }
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException("PaymentService: payment not found for booking " + bookingUid);
            if (ex.getStatusCode().equals(HttpStatus.NO_CONTENT))
                throw new NoContentException("Booking processed " + bookingUid);
        }
    }

    private void constructReport(Reservation reservation, String status) throws InternalServerErrorException {
        ReportAdditionReqRes request = new ReportAdditionReqRes();

        request.setHotelUid(reservation.getHotelUid().toString());
        request.setDateIn(reservation.getDateIn());
        request.setDateOut(reservation.getDateOut());
        request.setDateReserved(Date.from(Instant.now()));
        request.setRoomNumber(reservation.getRoomNumber());
        request.setStatus(status);
        request.setBookingUid(reservation.getBookingUid().toString());
        request.setUserUid(reservation.getUserUid().toString());

        try {
            queueProducer.produce(request);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException("Internal server error: JsonProcessingException " + e.getMessage());
        }
    }

    private void constructPaymentUpdate(String paymentUid, Boolean pay) throws InternalServerErrorException {
        PaymentUpdate update = new PaymentUpdate();
        update.setPaymentUid(paymentUid);
        update.setPay(pay);

        try {
            queueProducer.produce(update);
        } catch (JsonProcessingException e) {
            throw new InternalServerErrorException("Internal server error: JsonProcessingException " + e.getMessage());
        }
    }

}
