package com.rsoi.reports.repository;

import com.rsoi.reports.model.Report;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportsRepository extends CrudRepository<Report, Integer>
{
    Optional<Report> findByBookingUid(String bookingUid);

    Iterable<Report> findByDateInBeforeAndDateOutAfter(Date dateIn, Date dateOut);

    Iterable<Report> findByUserUid(String userUid);

    Iterable<Report> findByDateInEqualsOrDateOutEquals(Date dateIn, Date dateOut);
}
