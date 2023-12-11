//package com.example.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//@Configuration
//public class CustomOAuth2Config {
//    @Bean
//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {
//        return new CustomOAuth2UserServiceImpl();
//    }
//
//    static class CustomOAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//        @Override
//        public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
////             Implement your custom logic to load the OAuth2User here.
////             You can retrieve user details from the userRequest.
////             Example:
//            String username = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
////            String email = userRequest.getClientRegistration().getProviderDetails().getAuthorizationUri(); // Replace with the actual logic to retrieve the email
//
//            // Create an OAuth2User object with the user attributes.
//            OAuth2User user = new DefaultOAuth2User(
//                    getUserAuthorities(), // Implement a method to provide user authorities
//                    userRequest.getAdditionalParameters(), // You can add any additional parameters here
//                    username // This should be the username or identifier for the user
//            );
//
//            // Return the OAuth2User object.
//            return user;
//        }
//        private Collection<? extends GrantedAuthority> getUserAuthorities() {
//            // Implement logic to provide authorities/roles for the user
//            // For example, return a list of SimpleGrantedAuthority objects
//            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
//        }
//}
