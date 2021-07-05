package com.rsoi.hotels.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import model.Location;

import javax.persistence.*;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "Hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private int id;
    @Getter
    @Column(unique = true)
    @NonNull
    private UUID hotelUid = UUID.randomUUID();
    @Getter
    @Setter
    @NonNull
    private String name;
    @Getter
    @Setter
    @NonNull
    private UUID countryUid;
    @Getter
    @Setter
    @NonNull
    private UUID cityUid;
    @Getter
    @Setter
    @NonNull
    private String address;
    @Getter
    @Setter
    @NonNull
    private Integer numberRooms;

    public void setLocation(Location location) {
        this.countryUid = UUID.fromString(location.getCountry());
        this.cityUid = UUID.fromString(location.getCity());
        this.address = location.getAddress();
    }
}
