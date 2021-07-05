package reportreqres;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class ReportAdditionReqRes
{
    @Getter
    @Setter
    private String hotelUid;
    @Getter
    @Setter
    private String userUid;
    @Getter
    @Setter
    private String bookingUid;
    @Getter
    @Setter
    private Date dateIn;
    @Getter
    @Setter
    private Date dateOut;
    @Getter
    @Setter
    private Date dateReserved;
    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private Integer roomNumber;
}
