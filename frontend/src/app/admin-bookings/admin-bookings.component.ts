import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AdminBooking } from '../_models/adminBooking';
import { User } from '../_models/user';
import { ApiService } from '../_services';

@Component({
  selector: 'app-admin-bookings',
  templateUrl: './admin-bookings.component.html',
  styleUrls: ['./admin-bookings.component.scss']
})
export class AdminBookingsComponent implements OnInit {
  bookings: AdminBooking[] = [];
  form!: FormGroup;
  users: User[] = [];

  constructor(private fb: FormBuilder,
    private apiService: ApiService,
    private snackBar: MatSnackBar) { }

  onUserChanged(userUid: string) {
    console.log(userUid);
    this.apiService.getAdminBooking(userUid).subscribe(
      (res) => { this.bookings = res; },
      (err) => {
        console.log(err);
        this.snackBar.open('Ошибка, попробуйте позже');
      }
    );
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      user: ['']
    });
    this.apiService.getUsers().subscribe(
      (res) => {
        this.users = res;
      },
      (err) => {
        console.log(err);
        this.snackBar.open('Ошибка, попробуйте позже');
      }
    )
  }

}
