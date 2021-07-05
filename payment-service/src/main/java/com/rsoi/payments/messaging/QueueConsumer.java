package com.rsoi.payments.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsoi.payments.service.DatabaseService;
import exceptions.InternalServerErrorException;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import paymentreqres.PaymentUpdate;

@Component
@EnableRabbit
public class QueueConsumer
{

    @Autowired
    private DatabaseService db;

    @RabbitListener(queues = "payment-service")
    public void receiveMessage(String message) throws InternalServerErrorException
    {
        processMessage(message);
    }

    /*@RabbitListener(queues = "payment-service")
    public void receiveMessage(byte[] message) throws InternalServerErrorException
    {

        String strMessage = new String(message);
        processMessage(strMessage);
    }*/

    private void processMessage(String message) throws InternalServerErrorException
    {
        try
        {
            PaymentUpdate request = new ObjectMapper().readValue(message, PaymentUpdate.class);
            System.out.println("PROCESSING: " + request);
            if (request.getPay())
                db.updatePayment(request.getPaymentUid());
            else
                db.deletePayment(request.getPaymentUid());

        }
        catch (Exception e)
        {
            throw new InternalServerErrorException("Internal Server error happened on ReportService");
        }
    }
}
