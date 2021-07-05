package com.rsoi.hotels.service;


import com.rsoi.hotels.model.City;
import com.rsoi.hotels.model.Country;
import com.rsoi.hotels.model.Hotel;
import com.rsoi.hotels.model.Room;
import com.rsoi.hotels.repository.CityRepository;
import com.rsoi.hotels.repository.CountryRepository;
import com.rsoi.hotels.repository.HotelRepository;
import com.rsoi.hotels.repository.RoomRepository;
import hotelreqres.HotelAdditionRequest;
import model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = false)
public class DatabaseService {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CityRepository cityRepository;

    public Iterable<Hotel> getHotels(String country, String city) {
        if (country == null && city == null)
            return hotelRepository.findAll();
        else if (country != null && city == null)
            return hotelRepository.findByCountryUid(UUID.fromString(country));
        else if (country == null)
            return hotelRepository.findByCityUid(UUID.fromString(city));
        else
            return hotelRepository.findByCountryUidAndCityUid(UUID.fromString(country), UUID.fromString(city));
    }

    public Hotel getHotel(String hotelUid) {
        Optional<Hotel> hotelOptional = hotelRepository.findByHotelUid(UUID.fromString(hotelUid));
        Hotel hotel = null;
        if (hotelOptional.isPresent())
            hotel = hotelOptional.get();
        return hotel;
    }

    public String getPriceRange(String hotelUid) {
        Iterable<Room> rooms = roomRepository.findByHotelUidOrderByPrice(UUID.fromString(hotelUid));
        List<Room> roomsFound = new ArrayList<>();
        rooms.iterator().forEachRemaining(roomsFound::add);
        Integer minimum = roomsFound.get(0).getPrice();
        Integer maximum = roomsFound.get(roomsFound.size() - 1).getPrice();
        return minimum + " - " + maximum;
    }

    public List<Room> getRooms(String hotelUid, Integer numberGuests) {
        Iterable<Room> rooms = roomRepository.findByHotelUid(UUID.fromString(hotelUid));
        List<Room> roomsFound = new ArrayList<>();
        if (numberGuests == null) {
            rooms.iterator().forEachRemaining(roomsFound::add);
        } else {
            for (Room room : rooms) {
                if (room.getNumberGuests().equals(numberGuests))
                    roomsFound.add(room);
            }
        }
        return roomsFound;
    }

    public Room getRoom(String hotelUid, Integer roomNumber) {
        Optional<Room> roomOptional = roomRepository.findByHotelUidAndRoomNumber(UUID.fromString(hotelUid), roomNumber);
        Room room = null;
        if (roomOptional.isPresent())
            room = roomOptional.get();
        return room;
    }

    public UUID saveNewHotel(HotelAdditionRequest request) {
        Hotel hotel = saveHotelInfo(request);
        if (hotel != null) {
            saveRoomsInfo(hotel, request);
            return hotel.getHotelUid();
        }
        return null;
    }

    public Iterable<Country> getCountries() {
        return countryRepository.findAll();
    }

    public Country getCountry(UUID countryUid) {
        Optional<Country> country = countryRepository.findByCountryUid(countryUid);
        Country found = null;
        if (country.isPresent()) {
            found = country.get();
        }
        return found;
    }

    public City getCity(UUID cityUid) {
        Optional<City> city = cityRepository.findByCityUid(cityUid);
        City found = null;
        if (city.isPresent()) {
            found = city.get();
        }
        return found;
    }

    public Iterable<City> getCities(String countryUid) {
        Optional<Country> country = countryRepository.findByCountryUid(UUID.fromString(countryUid));
        Country found = null;
        if (country.isPresent()) {
            found = country.get();
            return found.getCities();
        }
        return new ArrayList<>();
    }

    private Hotel saveHotelInfo(HotelAdditionRequest request) {
        Boolean check = checkLocation(request.getLocation());
        if (check) {
            Hotel hotel = new Hotel();
            hotel.setName(request.getName());
            hotel.setLocation(request.getLocation());
            hotel.setNumberRooms(request.getRooms().getNumberRooms());
            return hotelRepository.save(hotel);
        }
        return null;
    }

    private Boolean checkLocation(Location location) {
        Boolean check = false;
        Optional<Country> country = countryRepository.findByCountryUid(UUID.fromString(location.getCountry()));
        Country found = null;
        if (country.isPresent()) {
            found = country.get();
            Optional<City> city = cityRepository.findByCityUid(UUID.fromString(location.getCity()));
            City foundCity = null;
            if (city.isPresent()) {
                foundCity = city.get();
                if (found.getCities().contains(foundCity))
                    check = true;
            }
        }
        return check;
    }

    private void saveRoomsInfo(Hotel hotel, HotelAdditionRequest request) {
        List<Integer> roomCosts = request.getRooms().getPricesForNumberGuests();
        int numberRoomsPerNumberGuests = hotel.getNumberRooms() / roomCosts.size();
        List<Room> newRooms = new ArrayList<>();
        int k = 1;
        for (int i = 0; i < roomCosts.size(); i++) {
            Integer price = roomCosts.get(i);
            for (int j = 0; j < numberRoomsPerNumberGuests; j++) {
                Room room = new Room();
                room.setHotelUid(hotel.getHotelUid());
                room.setNumberGuests(i + 1);
                room.setRoomNumber(k);
                room.setPrice(price);
                k++;
                newRooms.add(room);
            }
        }
        if (newRooms.size() < hotel.getNumberRooms()) {
            Integer price = roomCosts.get(0);
            for (int i = 0; i < hotel.getNumberRooms() - newRooms.size(); i++) {
                Room room = new Room();
                room.setHotelUid(hotel.getHotelUid());
                room.setNumberGuests(1);
                room.setRoomNumber(k);
                room.setPrice(price);
                k++;
                newRooms.add(room);
            }
        }
        roomRepository.saveAll(newRooms);
    }

}
