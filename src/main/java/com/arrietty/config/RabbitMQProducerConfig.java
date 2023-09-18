package com.arrietty.config;



import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfig {
    @Bean
    public Queue advertisementQueue() {
        return new Queue("AdvertisementQueue",true);
    }

    @Bean
    DirectExchange advertisementDirectExchange() {
        return new DirectExchange("AdvertisementDirectExchange",true,false);
    }

    @Bean
    Binding advertisementBindingDirect() {
        return BindingBuilder.bind(advertisementQueue()).to(advertisementDirectExchange()).with("AdvertisementDirectRouting");
    }

    @Bean
    public Queue tapQueue() {
        return new Queue("TapQueue",true);
    }

    @Bean
    DirectExchange tapDirectExchange() {
        return new DirectExchange("TapDirectExchange",true,false);
    }

    @Bean
    Binding tapBindingDirect() {
        return BindingBuilder.bind(tapQueue()).to(tapDirectExchange()).with("TapDirectRouting");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate mqTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
