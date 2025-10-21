package org.example.springprojektzespolowy.config;

import org.example.springprojektzespolowy.services.SecurityService;
import org.example.springprojektzespolowy.services.TestSecurityService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@TestConfiguration
@EnableWebSecurity
@EnableMethodSecurity // Włącza @PreAuthorize, ale używa TestSecurityService
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityService securityService() {
        return new TestSecurityService();
    }

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .securityContext(AbstractHttpConfigurer::disable)
            .sessionManagement(AbstractHttpConfigurer::disable)
            .requestCache(AbstractHttpConfigurer::disable)
            .anonymous(AbstractHttpConfigurer::disable)
            .build();
    }
}
