import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Hotel, Loyalty } from 'src/app/_models/';
import { ApiService, AuthenticationService } from 'src/app/_services';

@Component({
  selector: 'app-hotel-list',
  templateUrl: './hotel-list.component.html',
  styleUrls: ['./hotel-list.component.scss']
})
export class HotelListComponent implements OnInit {
  guestNumber!: number;
  hotels!: Hotel[];
  discount: number | null = null;

  onHotelSelected(hotelUid: string) {
    this.apiService.selectedHotel = this.hotels.find(h => h.hotelUid == hotelUid) as Hotel;
    this.router.navigate(['/book']);
  }

  constructor(
    private apiService: ApiService,
    private authService: AuthenticationService,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.apiService.getHotels().subscribe(
      (res) => {
        this.hotels = res;

        if (this.authService.isLogedIn()) {
          this.apiService.getLoyalty().subscribe(
            (res) => {
              if (res.discount > 0) {
                this.discount = res.discount;
                this.apiService.discount = this.discount;
                this.hotels.forEach(h => h.price *= 1 - (res.discount / 100));
              }
            },
            (err) => {
              console.log(err);
            });
        }
      },
      (err) => {
        this.snackBar.open('Ошибка, повторите позже');
        console.log(err);
      }
    );
  }
}
