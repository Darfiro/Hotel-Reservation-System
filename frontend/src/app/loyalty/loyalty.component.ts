import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Loyalty } from '../_models';
import { ApiService } from '../_services';

@Component({
  selector: 'app-loyalty',
  templateUrl: './loyalty.component.html',
  styleUrls: ['./loyalty.component.scss']
})
export class LoyaltyComponent implements OnInit {
  loyalty: Loyalty | null = null;

  constructor(private apiService: ApiService, private snackBar: MatSnackBar) { }

  ngOnInit(): void {
    this.apiService.getLoyalty().subscribe(
      (res) => {
        this.loyalty = res;
      },
      (err) => {
        console.log(err);
        if (err.status == 500) {
          this.snackBar.open('Ошибка, попробуйте позже');
        }
      }
    );
  }

}
