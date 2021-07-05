package hotelreqres;

import lombok.Getter;
import lombok.Setter;
import model.CityResponse;

import java.util.List;

public class LocationsResponse {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String uid;
    @Getter
    @Setter
    private List<CityResponse> cities;
}
