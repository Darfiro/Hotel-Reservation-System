package com.rsoi.reports.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsoi.reports.service.DatabaseService;
import exceptions.InternalServerErrorException;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reportreqres.ReportAdditionReqRes;

@Component
@EnableRabbit
public class QueueConsumer
{

    @Autowired
    private DatabaseService db;

    @RabbitListener(queues = "report-service")
    public void receiveMessage(String message) throws InternalServerErrorException
    {
        processMessage(message);
    }

    /*@RabbitListener(queues = "report-service")
    public void receiveMessage(byte[] message) throws InternalServerErrorException
    {
        String strMessage = new String(message);
        processMessage(strMessage);
    }*/

    private void processMessage(String message) throws InternalServerErrorException
    {
        try
        {
            ReportAdditionReqRes request = new ObjectMapper().readValue(message, ReportAdditionReqRes.class);
            System.out.println("PROCESSING: user " + request.getUserUid() + " hotel " + request.getHotelUid());
            db.postReportData(request);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException("Internal Server error happened on ReportService");
        }
    }
}

