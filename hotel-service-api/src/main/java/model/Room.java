package model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Room
{
    @Getter
    @Setter
    private Integer numberRooms;
    @Getter
    @Setter
    private List<Integer> pricesForNumberGuests;
}
