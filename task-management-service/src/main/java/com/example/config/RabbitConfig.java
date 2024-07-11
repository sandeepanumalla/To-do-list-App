package com.example.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;


@Configuration
public class RabbitConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("localhost");
//        connectionFactory.setHost("rabbitmq");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPort(5672);
        return connectionFactory;
    }

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.queue.analytics}")
    private String taskAnalyticsQueue;

    @Value("${rabbitmq.routing.analytics.key}")
    private String taskAnalyticsRoutingKey;


    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchangeName);
    }


    @Bean
    public Queue taskAnalyticsQueue() {
        return new Queue(taskAnalyticsQueue);
    }


    @Bean
    public Queue queue() {
        return new Queue(queueName);
    }


    @Bean
    public Binding bind(DirectExchange directExchange) {
        return BindingBuilder
                .bind(queue())
                .to(directExchange)
                .with(routingKey);
    }

    @Bean
    public Binding anotherBind(DirectExchange directExchange) {
        return BindingBuilder
                .bind(taskAnalyticsQueue())
                .to(directExchange)
                .with(taskAnalyticsRoutingKey);
    }

    @Bean
    public MessageConverter getMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(getMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPrefetchCount(10); // Set your desired prefetch count here
        factory.setDefaultRequeueRejected(false);
        // Other configurations...
        return factory;
    }

//    @Bean
//    public TopicExchange topicExchange() {
//        return new TopicExchange("notification-exchange");
//    }

//    @Bean
//    public Queue userQueue() {
//        return QueueBuilder.durable("user-queue").build();
//    }

//    @Bean
//    public Queue acknowledgmentQueue() {
//        return QueueBuilder.durable("acknowledgment-queue").build();
//    }

//    @Bean
//    public Binding userQueryBinding() {
//        return BindingBuilder.bind(userQueue()).to(topicExchange()).with("user.#");
//    }
//
//    @Bean
//    public Binding acknowledgmentBinding() {
//        return BindingBuilder.bind(userQueue()).to(topicExchange()).with("acknowledgment.#");
//    }

}
