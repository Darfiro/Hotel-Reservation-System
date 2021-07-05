package com.rsoi.booking.service;


import bookingreqres.BookingRoomRequest;
import com.rsoi.booking.model.Reservation;
import com.rsoi.booking.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = false)
public class DatabaseService
{
    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation saveReservation(String userUid, BookingRoomRequest request, String paymentUid)
    {
        Reservation reservation = new Reservation();
        reservation.setHotelUid(UUID.fromString(request.getHotelUid()));
        reservation.setDateIn(request.getBookingInterval().getDateIn());
        reservation.setDateOut(request.getBookingInterval().getDateOut());
        reservation.setUserUid(UUID.fromString(userUid));
        reservation.setRoomNumber(request.getRoomNumber());
        reservation.setPaymentUid(UUID.fromString(paymentUid));
        return reservationRepository.save(reservation);
    }

    public Iterable<Reservation> getReservations(String userUid)
    {
        return reservationRepository.findByUserUid(UUID.fromString(userUid));
    }

    public Reservation getReservation(String bookingUid)
    {
        Optional<Reservation> reservationOptional = reservationRepository.findByBookingUid(UUID.fromString(bookingUid));
        Reservation reservation = null;
        if (reservationOptional.isPresent())
            reservation = reservationOptional.get();
        return reservation;
    }

    public Iterable<Reservation> getReservationsByParams(String hotelUid, Integer roomNumber, Date dateIn, Date dateOut)
    {
        Iterable<Reservation> reservations = reservationRepository.findByDateInBetweenOrDateOutBetween(dateIn,
                dateOut,
                dateIn,
                dateOut);
        List<Reservation> listReservations = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getHotelUid().toString().equals(hotelUid) && reservation.getRoomNumber() == roomNumber) {
                listReservations.add(reservation);
            }
        }
        return listReservations;
    }

    public Iterable<Reservation> getReservationsByParams(String hotelUid, Date dateIn, Date dateOut)
    {
        Iterable<Reservation> reservations = reservationRepository.findByDateInBetweenOrDateOutBetween(dateIn,
                dateOut,
                dateIn,
                dateOut);
        List<Reservation> listReservations = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getHotelUid().toString().equals(hotelUid)) {
                listReservations.add(reservation);
            }
        }
        return listReservations;
    }
}
