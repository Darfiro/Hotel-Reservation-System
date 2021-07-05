package com.rsoi.hotels.repository;

import com.rsoi.hotels.model.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CityRepository   extends CrudRepository<City, Integer> {
    Optional<City> findByCityUid(UUID cityUid);
}
