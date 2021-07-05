package com.rsoi.hotels.repository;

import com.rsoi.hotels.model.Country;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.rmi.server.UID;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CountryRepository  extends CrudRepository<Country, Integer> {
    Optional<Country> findByName(String name);

    Optional<Country> findByCountryUid(UUID countryUid);
}
