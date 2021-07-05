package com.rsoi.loyalty.model;

import lombok.Getter;

public class LoyaltyStatus
{
    public enum Status {
        NO_STATUS("No status", 0),
        BRONZE("Bronze", 2),
        SILVER("Silver", 3),
        GOLD("Gold", 5);

        @Getter
        private String statusName;
        @Getter
        private Integer reservationsNeeded;

        Status(String status, Integer numberOfReservations)
        {
            statusName = status;
            reservationsNeeded = numberOfReservations;
        }

        @Override
        public String toString()
        {
            return statusName;
        }

    }
}
