import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { User } from '../_models/user';
import { ApiService } from '../_services';

@Component({
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html',
  styleUrls: ['./admin-users.component.scss']
})
export class AdminUsersComponent implements OnInit {
  users!: User[];

  constructor(private apiService: ApiService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.apiService.getUsers().subscribe(
      (res) => {
        this.users = res;
      },
      (err) => {
        this.snackBar.open('Ошибка, попробуйте позже');
        console.log(err);
      }
    );
  }
}
