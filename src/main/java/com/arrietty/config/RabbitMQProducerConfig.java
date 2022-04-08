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
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        //   return new Queue("TestDirectQueue",true,true,false);

        //一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue("AdvertisementQueue",true);
    }

    //Direct交换机 起名：TestDirectExchange
    @Bean
    DirectExchange advertisementDirectExchange() {
        //  return new DirectExchange("TestDirectExchange",true,true);
        return new DirectExchange("AdvertisementDirectExchange",true,false);
    }

    //绑定  将队列和交换机绑定, 并设置用于匹配键：TestDirectRouting
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

    //使用jackson做序列化
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
