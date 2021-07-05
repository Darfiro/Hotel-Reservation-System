import { DatePipe } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Loyalty, Room, Country, Hotel, Booking, Reservation } from '../_models';
import { AdminBooking } from '../_models/adminBooking';
import { CreateHotel } from '../_models/createHotel';
import { NewUser } from '../_models/newUser';
import { User } from '../_models/user';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  dateFormat: string = 'yyyy-MM-dd';
  country!: string;
  city!: string;
  dateFrom!: Date;
  dateTo!: Date;
  guestNumber!: number;
  selectedHotel!: Hotel;
  discount: number | null = null;

  getHotel(hotelUid: string) {
    return this.httpClient.get(`${environment.apiBaseUrl}/hotels/${hotelUid}`, {}) as Observable<Hotel>;
  }

  getCountries() {
    return this.httpClient.get(environment.apiBaseUrl + '/locations', {}) as Observable<Country[]>;
  }

  getHotels() {
    let requestParams = new HttpParams();
    requestParams = requestParams.appendAll({
      'country': this.country as string,
      'city': this.city as string,
      'dateIn': this.datePipe.transform(this.dateFrom, this.dateFormat) as string,
      'dateOut': this.datePipe.transform(this.dateTo, this.dateFormat) as string,
      'guestNumber': this.guestNumber as number
    });

    return this.httpClient.get(environment.apiBaseUrl + '/hotels', { params: requestParams }) as Observable<Hotel[]>;
  }

  getAllHotels() {
    return this.httpClient.get<Hotel[]>(environment.apiBaseUrl + '/hotels', {});
  }

  getHotelRooms() {
    let requestParams = new HttpParams();
    requestParams = requestParams.appendAll({
      'dateIn': this.datePipe.transform(this.dateFrom, this.dateFormat) as string,
      'dateOut': this.datePipe.transform(this.dateTo, this.dateFormat) as string,
      'numberGuests': this.guestNumber as number
    });
    return this.httpClient.get(environment.apiBaseUrl + `/hotels/${this.selectedHotel.hotelUid}/rooms`, { params: requestParams }) as Observable<Room[]>;
  }

  getAvailableRooms(hotelUid: string) {
    let requestParams = new HttpParams();
    requestParams = requestParams.appendAll({
      'dateIn': this.datePipe.transform(new Date(), this.dateFormat) as string,
      'dateOut': this.datePipe.transform(new Date(), this.dateFormat) as string
    });
    return this.httpClient.get<Room[]>(environment.apiBaseUrl + `/hotels/${hotelUid}/rooms`, { params: requestParams });
  }

  getLoyalty() {
    return this.httpClient.get(environment.apiBaseUrl + '/loyalty', {}) as Observable<Loyalty>;
  }

  bookHotelRoom(roomNumber: number, totalPrice: number) {
    let bookingInfo = new Booking(
      this.selectedHotel.hotelUid,
      roomNumber,
      totalPrice,
      this.datePipe.transform(this.dateFrom, this.dateFormat) as string,
      this.datePipe.transform(this.dateTo, this.dateFormat) as string
    );
    return this.httpClient.post(environment.apiBaseUrl + '/booking', bookingInfo, {});
  }

  getBookingList(): Observable<Reservation[]> {
    return this.httpClient.get<Reservation[]>(environment.apiBaseUrl + '/booking', {});
  }

  payBooking(bookingUid: string) {
    return this.httpClient.patch(`${environment.apiBaseUrl}/booking/${bookingUid}/pay`, {}, { observe: 'response' });
  }

  cancelBooking(bookingUid: string) {
    return this.httpClient.delete(`${environment.apiBaseUrl}/booking/${bookingUid}/refund`, { observe: 'response' });
  }

  getUsers() {
    return this.httpClient.get<User[]>(environment.apiBaseUrl + '/users', {});
  }

  createUser(newUser: NewUser) {
    return this.httpClient.post(environment.apiBaseUrl + '/users', newUser, {});
  }

  createHotel(newHotel: CreateHotel) {
    return this.httpClient.post(environment.apiBaseUrl + '/hotels', newHotel, {});
  }

  getAdminBooking(selectedUserUid: string) {
    let requestParams = new HttpParams().append('userUid', selectedUserUid);
    return this.httpClient.get<AdminBooking[]>(environment.apiBaseUrl + '/reports/booking', { params: requestParams });
  }

  changeRoomAvailability(hotelUid: string, numberRoom: number, state: boolean) {
    let status;
    if (!state) {
      status = 'Cancelled';
    } else {
      status = 'New';
    }
    return this.httpClient.patch(`${environment.apiBaseUrl}/hotels/${hotelUid}/rooms`, {
      rooms: [
        {
          number: numberRoom,
          interval: {
            dateIn: this.datePipe.transform(new Date(), this.dateFormat) as string,
            dateOut: this.datePipe.transform(new Date(), this.dateFormat) as string
          },
          status: status
        }
      ]
    });
  }

  constructor(private httpClient: HttpClient, private datePipe: DatePipe) { }
}
