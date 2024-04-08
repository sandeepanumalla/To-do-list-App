package com.example.filters;

import com.example.exceptions.JwtParsingException;
import com.example.model.User;
import com.example.utils.JwtAuthenticationProvider;
import com.example.utils.JwtService;
import com.example.utils.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final String[] allowedUrls = {"/api/auth/.*", "/test/.*",
            "/v3/api-docs/.*", "/v3/api-docs.*", "/swagger-ui/.*",
            "/swagger-ui.*",
            "/sign-in",
            "/oauth2/.*",
            "/task-management-sockets/.*"
    };


    Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final JwtService jwtService;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final TokenBlacklistService tokenBlacklistService;


    @Autowired
    public JwtAuthorizationFilter(JwtService jwtService, JwtAuthenticationProvider jwtAuthenticationProvider, TokenBlacklistService tokenBlacklistService) {
        this.jwtService = jwtService;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        for(String allowedPath: allowedUrls) {
            if(path.matches(allowedPath)) {
                logger.info("allowing the url " + path);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        logger.info("inside do Filter method");
        if(shouldNotFilter(request)) {
            logger.info("should not filter triggered");
            filterChain.doFilter(request, response);
        }
        if(request.getRequestURL().toString().equals("http://localhost:8081/task-management-sockets")) {

        }
        String token = extractToken(request);
        if(token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = "";
        try {
            username = jwtService.extractUsername(token);

        } catch (JwtParsingException e) {
            response.getWriter().println("Invalid or expired token. Please sign in again.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        User user = User.builder()
                .username(username)
                .build();
        if(!jwtService.isValidToken(token, user) || tokenBlacklistService.isRevokedToken(token)) {
            filterChain.doFilter(request, response);
        }
        if(!checkAuthentication(username)) {
            setAuthentication(username);
        }
        request.setAttribute("jwtToken", token);
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        Cookie httpCookie = null;
        if(request.getCookies() != null && request.getCookies().length > 0)  {
            httpCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("jwt"))
                    .findAny().orElse(null);
        }

        String token = null;
//        if(header == null && httpCookie == null) {
//            return null;
//        }
//        if(header != null && header.startsWith("Bearer ")) {
//            token = header.substring(7);
//        } else 
        if(httpCookie != null) {
            token = httpCookie.getValue();
        }
//        else {
//            token = header;
//        }
        return token;
    }

    private void setAuthentication(String username) {
        User user = jwtAuthenticationProvider.emailOrUsernameLookUp(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authenticated = jwtAuthenticationProvider.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticated);
    }

    private boolean checkAuthentication(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && authentication.getName().equals(username);
    }
}
