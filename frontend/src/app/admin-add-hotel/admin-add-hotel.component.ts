import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { City, Country } from '../_models';
import { CreateHotel } from '../_models/createHotel';
import { ApiService } from '../_services';

@Component({
  selector: 'app-admin-add-hotel',
  templateUrl: './admin-add-hotel.component.html',
  styleUrls: ['./admin-add-hotel.component.scss']
})
export class AdminAddHotelComponent implements OnInit {
  hotelForm!: FormGroup;
  countries: Country[] = [];
  cities!: City[];

  onCountryChanged(id: string) {
    this.cities = this.countries.filter(c => c.uid == id)[0].cities;
  }

  submit() {
    let newHotel = new CreateHotel(
      this.hotelForm.controls['name'].value,
      this.hotelForm.controls['roomsNumber'].value,
      this.hotelForm.controls['onePersonRoom'].value,
      this.hotelForm.controls['twoPersonRoom'].value,
      this.hotelForm.controls['threePersonRoom'].value,
      this.hotelForm.controls['fourPersonRoom'].value,
      this.hotelForm.controls['country'].value,
      this.hotelForm.controls['city'].value,
      this.hotelForm.controls['address'].value,
    );
    this.apiService.createHotel(newHotel).subscribe(
      () => {
        this.snackBar.open('Отель добавлен');
      },
      () => {
        this.snackBar.open('Не удалось добавить отель');
      }
    );
  }

  constructor(private formBuilder: FormBuilder, private apiService: ApiService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.apiService.getCountries().subscribe(res => {
      this.countries = res as Country[];
    }, (err) => {
      this.snackBar.open('Ошибка, повторите позже');
      console.log(err);
    });

    this.hotelForm = this.formBuilder.group({
      name: ['', Validators.required],
      roomsNumber: ['', Validators.required],
      onePersonRoom: ['', Validators.required],
      twoPersonRoom: ['', Validators.required],
      threePersonRoom: ['', Validators.required],
      fourPersonRoom: ['', Validators.required],
      country: ['', Validators.required],
      city: ['', Validators.required],
      address: ['', Validators.required],
    });
  }

}
