package com.vibhaneha.bookstore.orders.domain;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqTestRunner implements CommandLineRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            rabbitTemplate.convertAndSend("test-exchange", "test-routing-key", "Test Message");
            System.out.println("Connection to RabbitMQ is successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
