package com.rsoi.reports.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@Table(name="Report")
public class Report
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private int id;
    @Getter
    @Setter
    private String hotelUid;
    @Getter
    @Setter
    private String userUid;
    @Getter
    @Setter
    private String bookingUid;
    @Getter
    @Setter
    private Date dateIn;
    @Getter
    @Setter
    private Date dateOut;
    @Getter
    @Setter
    private Date dateReserved;
    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private Integer roomNumber;
    @Getter
    @Setter
    private Date dateReported;
}
