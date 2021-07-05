package reportreqres;

import lombok.Getter;
import lombok.Setter;

public class ReportFillingResponse
{
    @Getter
    @Setter
    private String hotelUid;
    @Getter
    @Setter
    private Integer roomsUnavailable;
}
