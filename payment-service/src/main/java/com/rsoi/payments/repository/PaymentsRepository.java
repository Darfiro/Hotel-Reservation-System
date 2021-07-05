package com.rsoi.payments.repository;

import com.rsoi.payments.model.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentsRepository extends CrudRepository<Payment, Integer>
{
    Optional<Payment> findByPaymentUid(UUID paymentUid);
}
