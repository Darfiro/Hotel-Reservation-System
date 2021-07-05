package com.rsoi.hotels.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "Rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private int id;
    @Getter
    @Setter
    @NonNull
    private Integer roomNumber;
    @Getter
    @Setter
    @NonNull
    private Integer numberGuests;
    @Getter
    @Setter
    @NonNull
    private Integer price;
    @Getter
    @Setter
    @NonNull
    private UUID hotelUid;
}
