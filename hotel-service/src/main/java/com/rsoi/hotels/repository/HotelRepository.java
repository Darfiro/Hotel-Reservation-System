package com.rsoi.hotels.repository;

import com.rsoi.hotels.model.Hotel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelRepository extends CrudRepository<Hotel, Integer> {
    Optional<Hotel> findByHotelUid(UUID hotelUid);

    Iterable<Hotel> findByCountryUid(UUID country);

    Iterable<Hotel> findByCityUid(UUID city);

    Iterable<Hotel> findByCountryUidAndCityUid(UUID country, UUID city);
}
