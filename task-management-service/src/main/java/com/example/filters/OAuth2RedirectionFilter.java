package com.example.filters;

import com.sun.jdi.request.InvalidRequestStateException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.io.IOException;

@WebFilter(
        filterName = "OAuth2RedirectionFilter",
        urlPatterns = {"/oauth2/sign-in", "/oauth2/register", "/oauth2/authorization/google"}
)
@Slf4j
public class OAuth2RedirectionFilter extends HttpFilter {

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        isUrlAuthorized(request);
        String requestType = resolveHttpSession(request);
        nullCheck(requestType);
        log.info("inside filter: " + requestType);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public String resolveHttpSession(HttpServletRequest request) {
        HttpSession httpSession = request.getSession();
        return (String) httpSession.getAttribute("OAuth2_Request_Type");
    }

    public void nullCheck(String requestType) {
        if(requestType == null) {
            throw new InvalidRequestStateException("Internal Server error");
        }
    }

    public void isUrlAuthorized(HttpServletRequest request) {
        if(request.getRequestURL().toString().contains("/oauth2/authorization/google")) {
            throw new InvalidRequestStateException("Not allowed");
        }
    }

}
