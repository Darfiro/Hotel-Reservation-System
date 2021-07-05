package com.rsoi.payments.model;

import lombok.Getter;

public class PaymentStatus
{
    public enum Status {
        NEW("New"),
        PAID("Paid"),
        REVERSED("Reversed"),
        CANCELLED("Cancelled");

        @Getter
        private String status;

        Status(String status)
        {
            this.status = status;
        }

        @Override
        public String toString()
        {
            return status;
        }
    }
}
