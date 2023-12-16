package com.example.config;

import com.example.filters.OAuth2RedirectionFilter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({ SecurityConfig.class, MailConfig.class, WebSocketsConfig.class})
public class MainConfig {

    private final OAuth2RedirectionFilter auth2RedirectionFilter;

    public MainConfig(OAuth2RedirectionFilter auth2RedirectionFilter) {
        this.auth2RedirectionFilter = auth2RedirectionFilter;
    }

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public FilterRegistrationBean<OAuth2RedirectionFilter> customFilter() {
        FilterRegistrationBean<OAuth2RedirectionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(auth2RedirectionFilter);
        return registrationBean;
    }
}
