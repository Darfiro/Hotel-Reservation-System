package bookingreqres;

import lombok.Getter;
import lombok.Setter;
import model.BookingInterval;

public class BookingInfoResponse
{
    @Getter
    @Setter
    private String hotelUid;
    @Getter
    @Setter
    private String bookingUid;
    @Getter
    @Setter
    private Integer roomNumber;
    @Getter
    @Setter
    private Float price;
    @Getter
    @Setter
    private BookingInterval bookingInterval;
    @Getter
    @Setter
    private String status;
}
