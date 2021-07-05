package com.rsoi.payments.service;

import com.rsoi.payments.model.Payment;
import com.rsoi.payments.model.PaymentStatus;
import com.rsoi.payments.repository.PaymentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paymentreqres.PaymentAdditionRequest;

import java.util.Optional;
import java.util.UUID;


@Service
@Transactional(readOnly = false)
public class DatabaseService
{
    @Autowired
    private PaymentsRepository paymentsRepository;

    public Payment postPayment(PaymentAdditionRequest request)
    {
        Payment payment = new Payment();
        payment.setPrice(request.getPrice());
        payment.setStatus(PaymentStatus.Status.NEW);
        return paymentsRepository.save(payment);
    }

    public Payment getPayment(String paymentUid)
    {
        Optional<Payment> paymentOptional = paymentsRepository.findByPaymentUid(UUID.fromString(paymentUid));
        Payment payment = null;
        if (paymentOptional.isPresent())
            payment = paymentOptional.get();
        return payment;
    }

    public String deletePayment(String paymentUid)
    {
        Optional<Payment> paymentOptional = paymentsRepository.findByPaymentUid(UUID.fromString(paymentUid));
        String newStatus = "";
        Payment payment = null;
        if (paymentOptional.isPresent())
        {
            payment = paymentOptional.get();
            if (payment.getStatus() == PaymentStatus.Status.NEW)
                payment.setStatus(PaymentStatus.Status.CANCELLED);
            else
                payment.setStatus(PaymentStatus.Status.REVERSED);
            newStatus = payment.getStatus().toString();
            paymentsRepository.save(payment);
        }
        return newStatus;
    }

    public void updatePayment(String paymentUid)
    {
        Optional<Payment> paymentOptional = paymentsRepository.findByPaymentUid(UUID.fromString(paymentUid));
        Payment payment = null;
        if (paymentOptional.isPresent())
        {
            payment = paymentOptional.get();
            payment.setStatus(PaymentStatus.Status.PAID);
            paymentsRepository.save(payment);
        }
    }
}
