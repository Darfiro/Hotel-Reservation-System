package model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Location
{
    @Getter
    @Setter
    private String country;
    @Getter
    @Setter
    private String city;
    @Getter
    @Setter
    private String address;
}
