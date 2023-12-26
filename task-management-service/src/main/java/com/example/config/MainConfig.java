package com.example.config;

import com.example.filters.OAuth2RedirectionFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

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
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public FilterRegistrationBean<OAuth2RedirectionFilter> customFilter() {
        FilterRegistrationBean<OAuth2RedirectionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(auth2RedirectionFilter);
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        registrationBean.addUrlPatterns("/oauth2/sign-in", "/oauth2/register", "/oauth2/authorization/google");
        return registrationBean;
    }
}
