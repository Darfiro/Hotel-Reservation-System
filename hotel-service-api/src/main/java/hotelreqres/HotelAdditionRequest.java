package hotelreqres;

import lombok.Getter;
import lombok.Setter;
import model.Location;
import model.Room;

public class HotelAdditionRequest
{
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private Room rooms;
    @Getter
    @Setter
    private Location location;
}
