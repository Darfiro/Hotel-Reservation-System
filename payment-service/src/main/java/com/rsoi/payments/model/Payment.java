package com.rsoi.payments.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name="Payments")
public class Payment
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Getter
    private int id;
    @Getter
    @Column(unique=true)
    @NonNull
    private UUID paymentUid = UUID.randomUUID();
    @Getter
    @Setter
    private Float price;
    @Getter
    @Setter
    private PaymentStatus.Status status;
}
