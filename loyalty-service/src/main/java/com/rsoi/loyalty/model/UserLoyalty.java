package com.rsoi.loyalty.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name="UserLoyalty")
public class UserLoyalty
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer loyaltyId;
    @Getter
    @Setter
    private UUID userUid;
    @Getter
    @Setter
    private Integer discount;
    @Getter
    @Setter
    private Integer reservationsNumber;
    @Getter
    @Setter
    private LoyaltyStatus.Status status;
}
