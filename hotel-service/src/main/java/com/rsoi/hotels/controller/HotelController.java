package com.rsoi.hotels.controller;


import bookingreqres.BookingRoomBookedResponse;
import bookingreqres.BookingRoomRequest;
import bookingreqres.BookingRoomUpdateResponse;
import com.rsoi.hotels.model.City;
import com.rsoi.hotels.model.Country;
import com.rsoi.hotels.model.Hotel;
import com.rsoi.hotels.model.Room;
import com.rsoi.hotels.service.DatabaseService;
import com.rsoi.hotels.service.RequestService;
import exceptions.*;
import hotelreqres.*;
import model.BookingInterval;
import model.CityResponse;
import model.RoomUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping(value = "/hotels")
public class HotelController {
    @Autowired
    private DatabaseService db;
    @Autowired
    private RequestService rs;

    @GetMapping("")
    public ResponseEntity<Iterable<HotelInfoResponse>> getHotels(@RequestParam(name = "country", required = false) String country,
                                                                 @RequestParam(name = "city", required = false) String city,
                                                                 @RequestParam(name = "numberGuests", required = false) Integer numberGuests,
                                                                 @RequestParam(name = "dateIn", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateIn,
                                                                 @RequestParam(name = "dateOut", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOut) {
        Iterable<Hotel> hotels = db.getHotels(country, city);
        List<HotelInfoResponse> response = new ArrayList<>();
        for (Hotel hotel : hotels) {
            List<Room> rooms = db.getRooms(hotel.getHotelUid().toString(), numberGuests);
            Integer price = rooms.get(0).getPrice();

            try {
                Integer roomsBooked = 0;
                BookingRoomBookedResponse booked = new BookingRoomBookedResponse();
                if (dateIn != null && dateOut != null) {
                    booked = hasSpareRooms(hotel.getHotelUid().toString(), dateIn, dateOut);
                    for (Integer roomNumber : booked.getBooked()) {
                        for (Room room : rooms) {
                            if (room.getRoomNumber() == roomNumber) {
                                roomsBooked++;
                            }
                        }
                    }
                }
                if (roomsBooked < booked.getBooked().size() || booked.getBooked().size() == 0) {
                    HotelInfoResponse res = new HotelInfoResponse();
                    res.setHotelUid(hotel.getHotelUid().toString());
                    String countryName = db.getCountry(hotel.getCountryUid()).getName();
                    String cityName = db.getCity(hotel.getCityUid()).getName();
                    res.setLocation(countryName + ", " + cityName + ", " + hotel.getAddress());
                    res.setName(hotel.getName());
                    res.setPrice(price);
                    res.setNumberRooms(hotel.getNumberRooms());
                    response.add(res);
                }
            } catch (BaseException e) {
                HotelInfoResponse res = new HotelInfoResponse();
                res.setHotelUid(hotel.getHotelUid().toString());
                String countryName = db.getCountry(hotel.getCountryUid()).getName();
                String cityName = db.getCity(hotel.getCityUid()).getName();
                res.setLocation(countryName + ", " + cityName + ", " + hotel.getAddress());
                res.setName(hotel.getName());
                res.setPrice(price);
                res.setNumberRooms(hotel.getNumberRooms());
                response.add(res);
            }
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hotelUid}")
    public ResponseEntity<HotelInfoResponse> getHotel(@PathVariable(name = "hotelUid") String hotelUid,
                                                      @RequestParam(name = "country", required = false) String country,
                                                      @RequestParam(name = "city", required = false) String city,
                                                      @RequestParam(name = "numberGuests", required = false) Integer numberGuests,
                                                      @RequestParam(name = "dateIn", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateIn,
                                                      @RequestParam(name = "dateOut", required = false)  @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOut) throws NotFoundException {
        Hotel hotel = db.getHotel(hotelUid);
        if (hotel == null)
            throw new NotFoundException("Hotel with uid " + hotelUid + " not found");
        HotelInfoResponse response = new HotelInfoResponse();
        List<Room> rooms = db.getRooms(hotel.getHotelUid().toString(), numberGuests);
        Integer price = rooms.get(0).getPrice();
        response.setHotelUid(hotelUid);
        response.setName(hotel.getName());
        response.setPrice(price);
        String countryName = db.getCountry(hotel.getCountryUid()).getName();
        String cityName = db.getCity(hotel.getCityUid()).getName();
        response.setLocation(countryName + ", " + cityName + ", " + hotel.getAddress());
        response.setNumberRooms(hotel.getNumberRooms());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{hotelUid}/rooms")
    public ResponseEntity<Iterable<RoomInfoResponse>> getRooms(@PathVariable(name = "hotelUid") String hotelUid,
                                                               @RequestParam(name = "numberGuests", required = false) Integer numberGuests,
                                                               @RequestParam(name = "dateIn")  @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateIn,
                                                               @RequestParam(name = "dateOut")  @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateOut) throws BadRequestException, ServiceUnavailableException, NotFoundException {
        if (dateIn == null || dateOut == null)
            throw new BadRequestException("Not all parameters are set: dates");
        Iterable<Room> rooms = db.getRooms(hotelUid, numberGuests);
        List<RoomInfoResponse> response = new ArrayList<>();
        try {
            BookingRoomBookedResponse booked = hasSpareRooms(hotelUid, dateIn, dateOut);
            if (booked != null) {
                for (Room room : rooms) {
                    boolean roomIsBooked = false;
                    for (Integer roomNumber : booked.getBooked()) {
                        if (roomNumber == room.getRoomNumber()) {
                            roomIsBooked = true;
                            break;
                        }
                    }
                    if (!roomIsBooked) {
                        RoomInfoResponse res = new RoomInfoResponse();
                        res.setNumber(room.getRoomNumber());
                        res.setPrice(room.getPrice());
                        response.add(res);
                    }
                }
            }
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("BookingService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException("BookingService: booking is not found");
            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST))
                throw new BadRequestException("BookingService: bad request");
        }
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userUid}/{hotelUid}/rooms")
    public void updateRooms(@PathVariable(name = "userUid") String userUid,
                            @PathVariable(name = "hotelUid") String hotelUid,
                            @RequestBody RoomsUpdateRequest request) throws BadRequestException, ServiceUnavailableException, NotFoundException {
        if (request.getRooms() == null || request.getRooms().size() == 0)
            throw new BadRequestException("Not all parameters are set");
        for (RoomUpdate room : request.getRooms()) {
            if (room.getInterval() == null || room.getNumber() == null || room.getStatus() == null ||
                    room.getInterval().getDateIn() == null || room.getInterval().getDateOut() == null)
                throw new BadRequestException("Not all parameters are set");
            try {
                ResponseEntity<Iterable<BookingRoomUpdateResponse>> bookingInfo =
                        rs.getBookingInfo(hotelUid, room.getNumber(), room.getInterval().getDateIn(), room.getInterval().getDateOut());
                if (bookingInfo.getBody() != null) {
                    Iterable<BookingRoomUpdateResponse> body = bookingInfo.getBody();
                    List<BookingRoomUpdateResponse> bookings = new ArrayList<>();
                    body.forEach(bookings::add);
                    if (bookings.size() == 0 && room.getStatus().equals("New")) {
                        postBooking(hotelUid, room, userUid);
                    }
                    for (BookingRoomUpdateResponse booking : body) {
                        String bookingUid = booking.getBookingUid();
                        String status = booking.getStatus();
                        if (!status.equals(room.getStatus())) {
                            if (room.getStatus().equals("Cancelled"))
                                rs.deleteBooking(bookingUid);
                            else {
                                postBooking(hotelUid, room, userUid);
                            }
                        }
                    }
                }
            } catch (HttpStatusCodeException ex) {
                if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                    throw new ServiceUnavailableException("BookingService is unavailable");
                if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                    throw new NotFoundException("BookingService: booking is not found");
                if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST))
                    throw new BadRequestException("BookingService: bad request");
            }
        }
    }

    @PostMapping("")
    public ResponseEntity postHotel(@RequestBody HotelAdditionRequest request) throws BadRequestException, NoContentException {
        if (request.getName() == null || request.getLocation() == null || request.getRooms() == null ||
                request.getLocation().getCountry() == null || request.getLocation().getCity() == null || request.getLocation().getAddress() == null ||
                request.getRooms().getNumberRooms() == null || request.getRooms().getPricesForNumberGuests() == null)
            throw new BadRequestException("Not all parameters set");
        UUID hotelUid = db.saveNewHotel(request);
        if (hotelUid == null) {
            throw new BadRequestException("Location is not correct");
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(hotelUid).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/locations")
    public ResponseEntity<Iterable<LocationsResponse>> getLocations() {
        Iterable<Country> countries = db.getCountries();
        List<LocationsResponse> responses = new ArrayList<>();
        for (Country country : countries) {
            LocationsResponse res = new LocationsResponse();
            res.setName(country.getName());
            res.setUid(country.getCountryUid().toString());
            Iterable<City> cities = db.getCities(country.getCountryUid().toString());
            List<CityResponse> citiesList = new ArrayList<>();
            for (City city : cities) {
                CityResponse cityItem = new CityResponse();
                cityItem.setName(city.getName());
                cityItem.setUid(city.getCityUid().toString());
                citiesList.add(cityItem);
            }
            res.setCities(citiesList);
            responses.add(res);
        }
        return ResponseEntity.ok(responses);
    }

    private void postBooking(String hotelUid, RoomUpdate room, String userUid) {
        Room dbRoom = db.getRoom(hotelUid, room.getNumber());
        BookingRoomRequest bookingRoomRequest = new BookingRoomRequest();
        bookingRoomRequest.setHotelUid(hotelUid);
        BookingInterval interval = new BookingInterval();
        interval.setDateIn(room.getInterval().getDateIn());
        interval.setDateOut(room.getInterval().getDateOut());
        bookingRoomRequest.setBookingInterval(interval);
        bookingRoomRequest.setPrice(Float.valueOf(dbRoom.getPrice()));
        bookingRoomRequest.setRoomNumber(room.getNumber());
        rs.postBooking(userUid, bookingRoomRequest);
    }

    private BookingRoomBookedResponse hasSpareRooms(String hotelUid, Date dateIn, Date dateOut) throws ServiceUnavailableException, NotFoundException, BadRequestException {
        try {
            ResponseEntity<BookingRoomBookedResponse> bookingInfo =
                    rs.getRoomBooked(hotelUid, dateIn, dateOut);
            return bookingInfo.getBody();
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode().equals(HttpStatus.SERVICE_UNAVAILABLE))
                throw new ServiceUnavailableException("BookingService is unavailable");
            if (ex.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new NotFoundException("BookingService: booking is not found");
            if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST))
                throw new BadRequestException("BookingService: bad request");
        }
        return null;
    }
}
