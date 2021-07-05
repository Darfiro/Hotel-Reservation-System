package model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class Interval
{
    @Getter
    @Setter
    private Date dateIn;
    @Getter
    @Setter
    private Date dateOut;
}
