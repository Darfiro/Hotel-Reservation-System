import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatIconModule } from "@angular/material/icon";
import { MatMenuModule } from "@angular/material/menu";
import { MatButtonModule } from "@angular/material/button";
import { DatePipe } from '@angular/common'

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from '@angular/material/core';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { MatSnackBar, MAT_SNACK_BAR_DEFAULT_OPTIONS } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { ApiService, AuthenticationService } from './_services';
import { HotelSearchComponent } from './hotel-search/hotel-search.component';
import { HotelListComponent } from './hotel-list/hotel-list.component';
import { BookingComponent } from './booking/booking.component';
import { LoginComponent } from './login/login.component';
import { BookingListComponent } from './booking-list/booking-list.component';
import { LoyaltyComponent } from './loyalty/loyalty.component';
import { JwtInterceptor } from './_helpers';
import { AdminUsersComponent } from './admin-users/admin-users.component';
import { AdminAddUserComponent } from './admin-add-user/admin-add-user.component';
import { AdminAddHotelComponent } from './admin-add-hotel/admin-add-hotel.component';
import { AdminBookingsComponent } from './admin-bookings/admin-bookings.component';
import { AdminRoomsAvailabilityComponent } from './admin-rooms-availability/admin-rooms-availability.component';



@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    HotelSearchComponent,
    HotelListComponent,
    BookingComponent,
    LoginComponent,
    BookingListComponent,
    LoyaltyComponent,
    AdminUsersComponent,
    AdminAddUserComponent,
    AdminAddHotelComponent,
    AdminBookingsComponent,
    AdminRoomsAvailabilityComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatMenuModule,
    MatButtonModule,
    MatToolbarModule,
    MatCardModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatSelectModule,
    HttpClientModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatSlideToggleModule
  ],
  bootstrap: [AppComponent],
  providers: [
    DatePipe,
    ApiService,
    AuthenticationService,
    MatSnackBar,
    MatSnackBar,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    },
    { provide: MAT_SNACK_BAR_DEFAULT_OPTIONS, useValue: { duration: 3000 } }
  ]
})
export class AppModule { }
