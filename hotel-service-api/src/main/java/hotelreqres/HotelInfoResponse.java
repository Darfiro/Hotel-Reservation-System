package hotelreqres;

import lombok.Getter;
import lombok.Setter;

public class HotelInfoResponse
{
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String hotelUid;
    @Getter
    @Setter
    private String location;
    @Getter
    @Setter
    private Integer price;
    @Getter
    @Setter
    private Integer numberRooms;
}
