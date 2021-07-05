import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/_services';
import { Hotel, Room } from 'src/app/_models';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.scss']
})
export class BookingComponent implements OnInit {
  dateFrom!: Date;
  dateTo!: Date;
  hotel!: Hotel;
  rooms!: Room[];

  book() {
    this.apiService.bookHotelRoom(this.rooms[0].number, this.totalPrice).subscribe(
      () => {
        this.router.navigate(['/booking-list']);
      },
      (err) => {
        this.snackBar.open('Ошибка, повторите позже');
        console.log(err);
      });
  }

  public get dateFromString(): string {
    return this.datePipe.transform(this.dateFrom, 'dd.MM.yyyy') as string;
  }

  public get dateToString(): string {
    return this.datePipe.transform(this.dateTo, 'dd.MM.yyyy') as string;
  }

  public get totalPrice(): number {
    let diff = this.dateTo.getTime() - this.dateFrom.getTime();
    let daysAmount = Math.ceil(diff / (1000 * 3600 * 24));

    let totalPrice = this.hotel.price * daysAmount;
    if (this.apiService.discount && this.apiService.discount > 0)
      totalPrice *= 1 - (this.apiService.discount / 100);

    return totalPrice;
  }

  constructor(
    private apiService: ApiService,
    private datePipe: DatePipe,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.dateFrom = this.apiService.dateFrom as Date;
    this.dateTo = this.apiService.dateTo as Date;
    this.hotel = this.apiService.selectedHotel as Hotel;

    this.apiService.getHotelRooms().subscribe(
      (rooms) => {
        this.rooms = rooms;
      },
      (err) => {
        this.snackBar.open('Ошибка, повторите позже');
        console.log(err);
      });
  }
}
