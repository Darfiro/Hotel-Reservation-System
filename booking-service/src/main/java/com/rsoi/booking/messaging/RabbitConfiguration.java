package com.rsoi.booking.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableRabbit
public class RabbitConfiguration
{
    @Value("${cloud.host}")
    private String host;
    @Value("${queue.name-report}")
    private String nameReport;
    @Value("${queue.name-loyalty}")
    private String nameLoyalty;
    @Value("${queue.name-payment}")
    private String namePayment;
    @Value("${fanout.exchange}")
    private String exchange;


    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setExchange(exchange);
        return rabbitTemplate;
    }

    @Bean
    public Queue myQueue1() {
        return new Queue(nameReport);
    }

    @Bean
    public Queue myQueue2() {
        return new Queue(nameLoyalty);
    }

    @Bean
    public Queue myQueue3() {
        return new Queue(namePayment);
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding loyaltyBinding(){
        return BindingBuilder.bind(myQueue1()).to(directExchange()).with("report");
    }

    @Bean
    public Binding reportBinding(){
        return BindingBuilder.bind(myQueue2()).to(directExchange()).with("loyalty");
    }

    @Bean
    public Binding paymentBinding(){
        return BindingBuilder.bind(myQueue2()).to(directExchange()).with("payment");
    }

    @Bean
    public ConnectionFactory connectionFactory()
    {
        String uri = host;
        URI url = null;
        try
        {
            url = new URI(uri);
        } catch (URISyntaxException e)
        {
            e.printStackTrace();
        }

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(url.getHost());
        connectionFactory.setUsername(url.getUserInfo().split(":")[0]);
        connectionFactory.setPassword(url.getUserInfo().split(":")[1]);
        if (!url.getPath().isEmpty())
            connectionFactory.setVirtualHost(url.getPath().replace("/", ""));
        connectionFactory.setConnectionTimeout(3000);
        connectionFactory.setRequestedHeartBeat(30);
        return connectionFactory;
    }


}