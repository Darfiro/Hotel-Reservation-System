import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Hotel } from '../_models';
import { Reservation } from '../_models/reservation';
import { ApiService } from '../_services';

@Component({
  selector: 'app-booking-list',
  templateUrl: './booking-list.component.html',
  styleUrls: ['./booking-list.component.scss']
})
export class BookingListComponent implements OnInit {
  data: {
    bookingInfo: Reservation,
    hotelInfo: Hotel
  }[] = [];

  pay(bookingUid: string) {
    this.apiService.payBooking(bookingUid).subscribe(
      () => {
        this.data.filter(d => d.bookingInfo.bookingUid == bookingUid)[0].bookingInfo.status = 'Paid';
      },
      (err) => {
        this.snackBar.open('Не удалось оплатить, попробуйте позже');
        console.log(err);
      }
    );
  }

  cancel(bookingUid: string) {
    this.apiService.cancelBooking(bookingUid).subscribe(
      () => {
        this.data.filter(d => d.bookingInfo.bookingUid == bookingUid)[0].bookingInfo.status = 'Cancelled';
      },
      (err) => {
        this.snackBar.open('Не удалось оплатить, попробуйте позже');
        console.log(err);
      }
    );
  }

  constructor(
    private apiService: ApiService,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit() {
    this.apiService.getBookingList().subscribe(
      (res) => {
        res.forEach(r => {
          this.apiService.getHotel(r.hotelUid).subscribe(
            (res) => {
              this.data.push({
                bookingInfo: r,
                hotelInfo: res
              });
            },
            (err) => {
              this.snackBar.open('Ошибка, попробуйте повторить позже');
              console.log(err);
            }
          )
        })
      },
      (err) => {
        this.snackBar.open('Ошибка, попробуйте повторить позже');
        console.log(err);
      }
    );
  }
}
