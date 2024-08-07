package com.vibhaneha.bookstore.orders.web.controllers;

/*
@RestController
public class RabbitMQDemoController {

    private final RabbitTemplate rabbitTemplate;

    private final ApplicationProperties properties;

    public RabbitMQDemoController(RabbitTemplate rabbitTemplate, ApplicationProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody MyMessage message){
        rabbitTemplate.convertAndSend(properties.orderEventsExchange(),message.routingKey(),message.myPayLoad());
    }

   public  record MyMessage(String routingKey,MyPayLoad myPayLoad){}
    public record MyPayLoad(String content){}
}
*/
