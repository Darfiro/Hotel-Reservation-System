export interface Reservation {
    hotelUid: string;
    bookingUid: string;
    roomNumber: number;
    price: number;
    status: string;
    bookingInterval: {
        dateIn: string;
        dateOut: string;
    }
}