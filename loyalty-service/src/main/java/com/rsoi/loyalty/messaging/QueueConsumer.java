package com.rsoi.loyalty.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsoi.loyalty.service.DatabaseService;
import exceptions.InternalServerErrorException;
import loyaltyreqres.LoyaltyUpdateRequest;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class QueueConsumer
{

    @Autowired
    private DatabaseService db;

    @RabbitListener(queues = "loyalty-service")
    public void receiveMessage(String message) throws InternalServerErrorException
    {
        processMessage(message);
    }

    /*@RabbitListener(queues = "loyalty-service")
    public void receiveMessage(byte[] message) throws InternalServerErrorException
    {

        String strMessage = new String(message);
        processMessage(strMessage);
    }*/

    private void processMessage(String message) throws InternalServerErrorException
    {
        try
        {
            LoyaltyUpdateRequest request = new ObjectMapper().readValue(message, LoyaltyUpdateRequest.class);
            System.out.println("PROCESSING: " + request.getUserUid());
            db.updateUserLoyalty(request.getUserUid());
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException("Internal Server error happened on LoyaltyService");
        }
    }
}
