package paymentreqres;

import lombok.Getter;
import lombok.Setter;

public class PaymentUpdate {
    @Getter
    @Setter
    private String paymentUid;
    @Getter
    @Setter
    private Boolean pay;
}
