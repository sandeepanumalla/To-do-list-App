package com.example.taskmanagementservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories("com.example.repository")
@ComponentScan("com.example")
@EntityScan("com.example.model")
@EnableCaching
@EnableJpaAuditing
@EnableRabbit
@EnableScheduling
//@PropertySource("classpath:application-dev.properties")
public class TaskManagementServiceApplication {


	public static void main(String[] args) {
			SpringApplication.run(TaskManagementServiceApplication.class, args);
	}

}
