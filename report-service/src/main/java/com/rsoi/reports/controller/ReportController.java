package com.rsoi.reports.controller;

import com.rsoi.reports.model.Report;
import com.rsoi.reports.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reportreqres.ReportBookingResponse;
import reportreqres.ReportFillingResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/reports/*")
public class ReportController
{
    @Autowired
    private DatabaseService db;


    @GetMapping("booking")
    public ResponseEntity<Iterable<ReportBookingResponse>> getReportBooking()
    {
        Iterable<Report> reports = db.getReports();
        List<ReportBookingResponse> response = new ArrayList<>();
        for(Report report : reports)
        {
            ReportBookingResponse res = new ReportBookingResponse();
            res.setHotelUid(report.getHotelUid());
            res.setUserUid(report.getUserUid());
            res.setDateIn(report.getDateIn());
            res.setDateOut(report.getDateOut());
            res.setDateReserved(report.getDateReserved());
            res.setRoomNumber(report.getRoomNumber());
            res.setStatus(report.getStatus());
            res.setDateReported(report.getDateReported());
            res.setBookingUid(report.getBookingUid());
            response.add(res);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("booking/{userUid}")
    public ResponseEntity<Iterable<ReportBookingResponse>> getReportBookingByUser(@PathVariable(name = "userUid") String userUid) {
        Iterable<Report> reports = db.getReports(userUid);
        List<ReportBookingResponse> responses = new ArrayList<>();
        for(Report report : reports)
        {
            ReportBookingResponse response = new ReportBookingResponse();
            response.setHotelUid(report.getHotelUid());
            response.setUserUid(report.getUserUid());
            response.setDateIn(report.getDateIn());
            response.setDateOut(report.getDateOut());
            response.setDateReserved(report.getDateReserved());
            response.setRoomNumber(report.getRoomNumber());
            response.setStatus(report.getStatus());
            response.setDateReported(report.getDateReported());
            response.setBookingUid(report.getBookingUid());
            responses.add(response);
        }
        return ResponseEntity.ok(responses);
    }

    @GetMapping("hotel-filling/{date}")
    public ResponseEntity<Iterable<ReportFillingResponse>> getReportFilling(@PathVariable(name="date") @DateTimeFormat(pattern = "dd.MM.yyyy") Date date)  {
        HashMap<String, List<Integer>> hotels = db.getReportsByDate(date);
        List<ReportFillingResponse> responses = new ArrayList<>();
        for(String hotelUid : hotels.keySet())
        {
            ReportFillingResponse response = new ReportFillingResponse();
            response.setHotelUid(hotelUid);
            response.setRoomsUnavailable(hotels.get(hotelUid).size());
            responses.add(response);

        }
        return ResponseEntity.ok(responses);
    }
}
