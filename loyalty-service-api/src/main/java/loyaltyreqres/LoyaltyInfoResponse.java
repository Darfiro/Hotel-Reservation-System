package loyaltyreqres;

import lombok.Getter;
import lombok.Setter;

public class LoyaltyInfoResponse
{
    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private Integer discount;

    public LoyaltyInfoResponse() {}

    public LoyaltyInfoResponse(String status, Integer discount)
    {
        this.status = status;
        this.discount = discount;
    }

}
