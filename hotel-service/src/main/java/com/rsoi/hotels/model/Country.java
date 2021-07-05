package com.rsoi.hotels.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "Countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private int id;
    @Getter
    @Column(unique = true)
    @NonNull
    private UUID countryUid = UUID.randomUUID();
    @Getter
    @Setter
    @NonNull
    private String name;
    @Getter
    @Setter
    @OneToMany(mappedBy = "country", fetch=FetchType.EAGER)
    private Set<City> cities;
}
