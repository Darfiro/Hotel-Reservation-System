package hotelreqres;

import lombok.Getter;
import lombok.Setter;
import model.RoomUpdate;

import java.util.List;

public class RoomsUpdateRequest
{
    @Getter
    @Setter
    private List<RoomUpdate> rooms;
}
