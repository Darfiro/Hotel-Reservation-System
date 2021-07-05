import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { NewUser } from '../_models/newUser';
import { ApiService } from '../_services';

@Component({
  selector: 'app-admin-add-user',
  templateUrl: './admin-add-user.component.html',
  styleUrls: ['./admin-add-user.component.scss']
})
export class AdminAddUserComponent implements OnInit {
  userForm!: FormGroup;

  submit() {
    let newUser = new NewUser(
      this.userForm.controls['login'].value,
      this.userForm.controls['password'].value,
      this.userForm.controls['role'].value,
    );
    this.apiService.createUser(newUser).subscribe(
      () => {
        this.router.navigate(['admin/users']);
      },
      (err) => {
        this.snackBar.open('Не получилось добавить пользователя');
        console.log(err);
      }
    )
  }

  constructor(private formBuilder: FormBuilder, private apiService: ApiService, private router: Router, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.userForm = this.formBuilder.group({
      login: ['', Validators.required],
      password: ['', Validators.required],
      role: ['', Validators.required],
    });
  }
}
