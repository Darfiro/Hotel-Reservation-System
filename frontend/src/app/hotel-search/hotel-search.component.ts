import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { Country, City } from 'src/app/_models';
import { ApiService } from 'src/app/_services';

@Component({
  selector: 'app-hotel-search',
  templateUrl: './hotel-search.component.html',
  styleUrls: ['./hotel-search.component.scss']
})
export class HotelSearchComponent implements OnInit {
  countries: Country[] = [];
  cities!: City[];
  form!: FormGroup;
  minDate!: Date;

  submit() {
    if (this.form.valid) {
      this.apiService.country = this.form.controls['country'].value;
      this.apiService.city = this.form.controls['city'].value;
      this.apiService.dateFrom = this.form.controls['dateFrom'].value;
      this.apiService.dateTo = this.form.controls['dateTo'].value;
      this.apiService.guestNumber = this.form.controls['guestNumber'].value;

      this.router.navigate(['hotel-list']);
    }
  }

  onCountryChanged(id: string) {
    this.cities = this.countries.filter(c => c.uid == id)[0].cities;
  }

  ngOnInit() {
    this.minDate = new Date();

    this.form = this.fb.group({
      country: ['', [Validators.required]],
      city: ['', [Validators.required]],
      dateFrom: ['', [Validators.required]],
      dateTo: ['', [Validators.required]],
      guestNumber: ['', [Validators.required]]
    });

    this.apiService.getCountries().subscribe(res => {
      this.countries = res as Country[];
    }, (err) => {
      this.snackBar.open('Ошибка, повторите позже');
      console.log(err);
    });
  }

  constructor(private fb: FormBuilder,
    private apiService: ApiService,
    private router: Router,
    private snackBar: MatSnackBar) { }
}
