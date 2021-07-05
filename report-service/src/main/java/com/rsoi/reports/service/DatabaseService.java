package com.rsoi.reports.service;

import com.rsoi.reports.model.Report;
import com.rsoi.reports.repository.ReportsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reportreqres.ReportAdditionReqRes;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.Instant;
import java.util.*;

@Service
@Transactional(readOnly = false)
public class DatabaseService {
    @Autowired
    private ReportsRepository reportsRepository;

    public void postReportData(ReportAdditionReqRes request) {
        Optional<Report> reportIn = reportsRepository.findByBookingUid(request.getBookingUid());
        if (reportIn.isPresent()) {
            Report report = reportIn.get();
            if (!request.getStatus().equals(report.getStatus())) {
                report.setStatus(request.getStatus());
                report.setDateReported(Date.from(Instant.now()));
                reportsRepository.save(report);
            }
        } else {
            Report report = new Report();
            report.setHotelUid(request.getHotelUid());
            report.setUserUid(request.getUserUid());
            report.setDateIn(request.getDateIn());
            report.setDateOut(request.getDateOut());
            report.setDateReserved(request.getDateReserved());
            report.setRoomNumber(request.getRoomNumber());
            report.setStatus(request.getStatus());
            report.setDateReported(Date.from(Instant.now()));
            report.setBookingUid(request.getBookingUid());
            reportsRepository.save(report);
        }
    }

    public Iterable<Report> getReports() {
        return reportsRepository.findAll();
    }

    public HashMap<String, List<Integer>> getReportsByDate(Date date) {
        HashMap<String, List<Integer>> hotels = new HashMap<>();
        List<Report> reports = getReports(date);
        for (Report report : reports) {
            if (hotels.get(report.getHotelUid()) != null) {
                List<Integer> rooms = hotels.get(report.getHotelUid());
                if (!rooms.contains(report.getRoomNumber()))
                    rooms.add(report.getRoomNumber());
                hotels.replace(report.getHotelUid(), rooms);
            }
            else {
                List<Integer> rooms = new ArrayList<>();
                rooms.add(report.getRoomNumber());
                hotels.put(report.getHotelUid(), rooms);
            }
        }
        return hotels;
    }

    private List<Report> getReports(Date date) {
        Iterable<Report> reportsBetween = reportsRepository.findByDateInBeforeAndDateOutAfter(date, date);
        Iterable<Report> reportsEqual = reportsRepository.findByDateInEqualsOrDateOutEquals(date, date);
        List<Report> reports = new ArrayList<>();
        for (Report report : reportsEqual) {
            if (report.getStatus().equals("New") || report.getStatus().equals("Paid"))
                reports.add(report);
        }
        for (Report report : reportsBetween) {
            if (report.getStatus().equals("New") || report.getStatus().equals("Paid"))
                reports.add(report);
        }
        return reports;
    }

    public Iterable<Report> getReports(String userUid) {
        Iterable<Report> reports = reportsRepository.findByUserUid(userUid);
        return reports;
    }

}
