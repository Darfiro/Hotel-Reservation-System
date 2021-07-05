package paymentreqres;

import lombok.Getter;
import lombok.Setter;

public class PaymentAdditionRequest
{
    @Getter
    @Setter
    private Float price;

    public PaymentAdditionRequest() {}

    public PaymentAdditionRequest(Float price)
    {
        this.price = price;
    }
}
