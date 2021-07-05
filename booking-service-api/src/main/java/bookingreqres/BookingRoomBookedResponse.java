package bookingreqres;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class BookingRoomBookedResponse
{
    @Getter
    @Setter
    private List<Integer> booked;

    public BookingRoomBookedResponse() {
        booked = new ArrayList<>();
    }

    public void addRoom(Integer room) {
        if (!booked.contains(room))
            booked.add(room);
    }
}
