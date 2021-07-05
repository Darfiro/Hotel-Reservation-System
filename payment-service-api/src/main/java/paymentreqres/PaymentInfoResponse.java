package paymentreqres;

import lombok.Getter;
import lombok.Setter;

public class PaymentInfoResponse
{
    @Getter
    @Setter
    private Float price;
    @Getter
    @Setter
    private String status;

    public PaymentInfoResponse() {}

    public PaymentInfoResponse(Float price, String status)
    {
        this.price = price;
        this.status = status;
    }
}
