package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class BookingInterval
{
    @Getter
    @Setter
    private Date dateIn;
    @Getter
    @Setter
    private Date dateOut;
}
