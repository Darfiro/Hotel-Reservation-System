package com.rsoi.booking.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name="Reservations")
public class Reservation
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private int id;
    @Getter
    @Column(unique=true)
    @NonNull
    private UUID bookingUid = UUID.randomUUID();
    @Getter
    @Setter
    private UUID hotelUid;
    @Getter
    @Setter
    private UUID userUid;
    @Getter
    @Setter
    private UUID paymentUid;
    @Getter
    @Setter
    private Integer roomNumber;
    @Getter
    @Setter
    private Date dateIn;
    @Getter
    @Setter
    private Date dateOut;
}
