package com.rsoi.loyalty.service;

import com.rsoi.loyalty.model.LoyaltyStatus;
import com.rsoi.loyalty.model.UserLoyalty;
import com.rsoi.loyalty.repository.UserLoyaltyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Service
@Transactional(readOnly = false)
public class DatabaseService
{
    @Autowired
    private UserLoyaltyRepository userLoyaltyRepository;

    public void updateUserLoyalty(String userUid)
    {
        Optional<UserLoyalty> loyaltyOptional = userLoyaltyRepository.findByUserUid(UUID.fromString(userUid));
        UserLoyalty loyalty = null;
        if (loyaltyOptional.isPresent())
        {
            loyalty = changeLoyalty(loyaltyOptional.get());
        }
        else
        {
            loyalty = new UserLoyalty();
            loyalty.setUserUid(UUID.fromString(userUid));
            loyalty.setDiscount(0);
            loyalty.setStatus(LoyaltyStatus.Status.NO_STATUS);
            loyalty.setReservationsNumber(1);
        }
        userLoyaltyRepository.save(loyalty);
    }

    public UserLoyalty getLoyalty(String userUid)
    {
        Optional<UserLoyalty> loyaltyOptional = userLoyaltyRepository.findByUserUid(UUID.fromString(userUid));
        UserLoyalty loyalty = null;
        if (loyaltyOptional.isPresent())
        {
            loyalty = loyaltyOptional.get();
        }
        return loyalty;
    }

    private UserLoyalty changeLoyalty(UserLoyalty loyalty)
    {
        loyalty.setReservationsNumber(loyalty.getReservationsNumber() + 1);
        switch (loyalty.getStatus())
        {
            case NO_STATUS:
                if (loyalty.getReservationsNumber() >= LoyaltyStatus.Status.BRONZE.getReservationsNeeded())
                {
                    loyalty.setStatus(LoyaltyStatus.Status.BRONZE);
                    loyalty.setDiscount(loyalty.getDiscount() + 5);
                }
                break;
            case BRONZE:
                if (loyalty.getReservationsNumber() >= LoyaltyStatus.Status.SILVER.getReservationsNeeded())
                {
                    loyalty.setStatus(LoyaltyStatus.Status.SILVER);
                    loyalty.setDiscount(loyalty.getDiscount() + 5);
                }
                break;
            case SILVER:
                if (loyalty.getReservationsNumber() >= LoyaltyStatus.Status.GOLD.getReservationsNeeded())
                {
                    loyalty.setStatus(LoyaltyStatus.Status.GOLD);
                    loyalty.setDiscount(loyalty.getDiscount() + 5);
                }
                break;
            case GOLD:
                break;
        }
        return loyalty;
    }
}
