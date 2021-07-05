export class BookingInterval {
    dateIn: string;
    dateOut: string;

    constructor(dateIn: string, dateOut: string) {
        this.dateIn = dateIn;
        this.dateOut = dateOut;
    }
}

export class Booking {
    hotelUid: string;
    roomNumber: number;
    price: number;
    bookingInterval: BookingInterval;

    constructor(hotelUid: string, roomNumber: number, price: number, dateIn: string, dateOut: string) {
        this.hotelUid = hotelUid;
        this.roomNumber = roomNumber;
        this.price = price;
        this.bookingInterval = new BookingInterval(dateIn, dateOut);
    }
}