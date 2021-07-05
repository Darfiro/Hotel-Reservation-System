export class CreateHotel {
    name: string;
    rooms: {
        numberRooms: number,
        pricesForNumberGuests: number[];
    };
    location: {
        country: string;
        city: string;
        address: string;
    };

    constructor(
        name: string,
        roomsNumber: number,
        onePersonPrice: number,
        twoPersonPrice: number,
        threePersonPrice: number,
        fourPersonPrice: number,
        country: string,
        city: string,
        address: string
    ) {
        this.name = name;
        this.rooms = {
            numberRooms: roomsNumber,
            pricesForNumberGuests: [
                onePersonPrice,
                twoPersonPrice,
                threePersonPrice,
                fourPersonPrice
            ]
        };
        this.location = {
            country: country,
            city: city,
            address: address
        }
    }
}