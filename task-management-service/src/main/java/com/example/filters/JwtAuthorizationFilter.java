package com.example.filters;

import com.example.model.User;
import com.example.utils.JwtAuthenticationProvider;
import com.example.utils.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;


    @Autowired
    public JwtAuthorizationFilter(JwtService jwtService, JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.jwtService = jwtService;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if(token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = jwtService.extractUsername(token);
        User user = User.builder()
                .username(username)
                .build();
        if(!jwtService.validateToken(token, user)) {
//            throw new BadCredentialsException("token is expired or invalid");
            filterChain.doFilter(request, response);
        }
        if(!checkAuthentication(username)) {
            setAuthentication(username);
        }
        filterChain.doFilter(request, response);

    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token;
        if(header == null) {
            return null;
        }
        if(header.startsWith("Bearer ")) {
            token = header.substring(7);
        } else {
            token = header;
        }

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
