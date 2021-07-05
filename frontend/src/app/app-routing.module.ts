import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AdminAddHotelComponent } from './admin-add-hotel/admin-add-hotel.component';
import { AdminAddUserComponent } from './admin-add-user/admin-add-user.component';
import { AdminBookingsComponent } from './admin-bookings/admin-bookings.component';
import { AdminRoomsAvailabilityComponent } from './admin-rooms-availability/admin-rooms-availability.component';
import { AdminUsersComponent } from './admin-users/admin-users.component';
import { BookingListComponent } from './booking-list/booking-list.component';
import { BookingComponent } from './booking/booking.component';
import { HotelListComponent } from './hotel-list/hotel-list.component';
import { HotelSearchComponent } from './hotel-search/hotel-search.component';
import { LoginComponent } from './login/login.component';
import { LoyaltyComponent } from './loyalty/loyalty.component';
import { AuthGuard } from './_helpers';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'search', component: HotelSearchComponent },
  { path: 'hotel-list', component: HotelListComponent },
  { path: 'book', component: BookingComponent, canActivate: [AuthGuard] },
  { path: 'booking-list', component: BookingListComponent, canActivate: [AuthGuard] },
  { path: 'loyalty', component: LoyaltyComponent, canActivate: [AuthGuard] },
  { path: 'admin/users', component: AdminUsersComponent, canActivate: [AuthGuard] },
  { path: 'admin/users/add', component: AdminAddUserComponent, canActivate: [AuthGuard] },
  { path: 'admin/add-hotel', component: AdminAddHotelComponent, canActivate: [AuthGuard] },
  { path: 'admin/bookings', component: AdminBookingsComponent, canActivate: [AuthGuard] },
  { path: 'admin/rooms-availability', component: AdminRoomsAvailabilityComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/search', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
