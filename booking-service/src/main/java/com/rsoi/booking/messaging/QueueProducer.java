package com.rsoi.booking.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import loyaltyreqres.LoyaltyUpdateRequest;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import paymentreqres.PaymentUpdate;
import reportreqres.ReportAdditionReqRes;

import java.sql.Date;
import java.time.Instant;

@Component
@EnableRabbit
public class QueueProducer
{
    @Value("${fanout.exchange}")
    private String fanoutExchange;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public QueueProducer(RabbitTemplate rabbitTemplate) {
        super();
        this.rabbitTemplate = rabbitTemplate;
    }

    public void produce(ReportAdditionReqRes reportAdditionReqRes) throws JsonProcessingException
    {
        System.out.println(Date.from(Instant.now()) + " putting in queue request " + reportAdditionReqRes.getDateReserved());
        rabbitTemplate.convertAndSend("report", new ObjectMapper().writeValueAsString(reportAdditionReqRes));
    }

    public void produce(LoyaltyUpdateRequest update) throws JsonProcessingException
    {
        System.out.println(Date.from(Instant.now()) + " putting in queue request " + update.getUserUid());
        rabbitTemplate.convertAndSend("loyalty", new ObjectMapper().writeValueAsString(update));
    }

    public void produce(PaymentUpdate update) throws JsonProcessingException
    {
        System.out.println(Date.from(Instant.now()) + " putting in queue request for payment " + update.getPaymentUid());
        rabbitTemplate.convertAndSend("payment", new ObjectMapper().writeValueAsString(update));
    }
}
