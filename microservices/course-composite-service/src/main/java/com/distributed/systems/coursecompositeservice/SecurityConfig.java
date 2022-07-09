package com.distributed.systems.coursecompositeservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


import static org.springframework.http.HttpMethod.*;

@EnableWebFluxSecurity
public class SecurityConfig {

    //Actuator unprotected, all other URLs require authenticated user and authorization will be based on JWT-encoded OAuth 2.0 token
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers(POST, "/course-composite/**").hasAuthority("SCOPE_course:write")
                .pathMatchers(DELETE, "/course-composite/**").hasAuthority("SCOPE_course:write")
                .pathMatchers(GET, "/course-composite/**").hasAuthority("SCOPE_course:read")
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt();
        return http.build();
    }
}
