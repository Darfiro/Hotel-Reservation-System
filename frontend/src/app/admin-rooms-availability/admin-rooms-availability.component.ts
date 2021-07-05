import { Component, OnInit } from '@angular/core';
import { ApiService } from '../_services';

interface RoomStatus {
  numberRoom: number,
  status: boolean,
}

@Component({
  selector: 'app-admin-rooms-availability',
  templateUrl: './admin-rooms-availability.component.html',
  styleUrls: ['./admin-rooms-availability.component.scss']
})
export class AdminRoomsAvailabilityComponent implements OnInit {
  displayedColumns: string[] = ['numberRoom', 'status'];
  data: {
    hotelName: string,
    hotelUid: string,
    numberRooms: number,
    roomSatuses: RoomStatus[]
  }[] = [];

  toggleChange(hotelUid: string, numberRoom: number, currentState: boolean) {
    this.apiService.changeRoomAvailability(hotelUid, numberRoom, currentState).subscribe(
      () => console.log('success'),
      () => console.log('failer'),
    )
  }

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.apiService.getAllHotels().subscribe(
      (hotels) => {
        hotels.forEach(hotel => {
          let roomStatuses: RoomStatus[] = [];
          for (let numberRoom = 1; numberRoom <= hotel.numberRooms; numberRoom++) {
            roomStatuses.push({
              numberRoom: numberRoom,
              status: false,
            });
          }

          this.apiService.getAvailableRooms(hotel.hotelUid).subscribe(
            (rooms) => {
              rooms.forEach(room => {
                roomStatuses.filter(r => r.numberRoom == room.number)[0].status = true;
              });
            },
            (err) => { console.log(err); }
          );

          this.data.push({
            hotelName: hotel.name,
            hotelUid: hotel.hotelUid,
            numberRooms: hotel.numberRooms,
            roomSatuses: roomStatuses
          });
        });
      },
      (err) => { console.log(err); }
    );
  }
}
