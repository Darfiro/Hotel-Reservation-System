package com.rsoi.payments.controller;

import com.rsoi.payments.model.Payment;
import com.rsoi.payments.service.DatabaseService;
import exceptions.BadRequestException;
import exceptions.NoContentException;
import exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import paymentreqres.PaymentAdditionRequest;
import paymentreqres.PaymentInfoResponse;

import java.net.URI;

@RestController
@RequestMapping(value = "/payments")
public class PaymentController
{
    @Autowired
    private DatabaseService db;

    /**
     * Creates payment
     * @param request contains price to pay
     * @return OK if payment was created, NotFound if price was not present in request
     * @throws BadRequestException
     */
    @PostMapping("")
    public ResponseEntity postPayment(@RequestBody PaymentAdditionRequest request) throws BadRequestException
    {
        if (request.getPrice() == null)
            throw new BadRequestException("No price in request");
        Payment payment = db.postPayment(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(payment.getPaymentUid()).toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * Updates payment (when paid)
     * @param paymentUid payment unique identifier
     * @throws NoContentException
     */
    @PatchMapping("/{paymentUid}")
    public void paymentPaid(@PathVariable(name="paymentUid") String paymentUid) throws NoContentException
    {
        db.updatePayment(paymentUid);
        throw new NoContentException("Payment processed " + paymentUid);
    }

    /**
     * Gets information about payment
     * @param paymentUid payment unique identifier
     * @return OK with price and status if payment is found, NotFound otherwise
     * @throws NotFoundException
     */
    @GetMapping("/{paymentUid}")
    public ResponseEntity<PaymentInfoResponse> getPayment(@PathVariable(name="paymentUid") String paymentUid) throws NotFoundException
    {
        Payment payment = db.getPayment(paymentUid);
        if (payment == null)
            throw new NotFoundException("Payment not found");
        PaymentInfoResponse response = new PaymentInfoResponse(payment.getPrice(), payment.getStatus().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Cancelles payment
     * @param paymentUid payment unique identifier
     * @throws NoContentException
     */
    @DeleteMapping("/{paymentUid}")
    public ResponseEntity<String> deletePayment(@PathVariable(name="paymentUid") String paymentUid) throws NoContentException
    {
        String status = db.deletePayment(paymentUid);
        return ResponseEntity.ok(status);
    }
}
