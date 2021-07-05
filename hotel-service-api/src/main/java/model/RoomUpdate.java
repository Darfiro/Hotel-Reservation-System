package model;

import lombok.Getter;
import lombok.Setter;

public class RoomUpdate
{
    @Getter
    @Setter
    private Integer number;
    @Getter
    @Setter
    private Interval interval;
    @Getter
    @Setter
    private String status;
}
