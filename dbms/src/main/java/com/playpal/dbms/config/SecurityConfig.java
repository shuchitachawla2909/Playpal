package com.playpal.dbms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // disables CSRF for simplicity (important for POST from Postman)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // allow all endpoints without login
                );
        return http.build();
    }
}
