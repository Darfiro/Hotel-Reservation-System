package com.rsoi.hotels.repository;

import com.rsoi.hotels.model.Room;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends CrudRepository<Room, Integer> {
    Iterable<Room> findByHotelUid(UUID hotelUid);

    Iterable<Room> findByHotelUidOrderByPrice(UUID hotelUid);

    Optional<Room> findByHotelUidAndRoomNumber(UUID hotelUid, Integer roomNumber);
}

