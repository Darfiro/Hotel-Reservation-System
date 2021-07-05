package com.rsoi.booking.repository;

import com.rsoi.booking.model.Reservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Integer>
{
    Optional<Reservation> findByBookingUid(UUID bookingUid);
    Iterable<Reservation> findByDateInBetweenOrDateOutBetween(Date dateIn, Date dateOut, Date dateInO, Date dateOutO);
    Iterable<Reservation> findByUserUid(UUID userUid);
}
