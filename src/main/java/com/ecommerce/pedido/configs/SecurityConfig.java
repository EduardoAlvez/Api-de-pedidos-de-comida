package com.ecommerce.pedido.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desabilita o CSRF. Essencial para APIs REST que não usam sessões/cookies.
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configura as regras de autorização para as requisições HTTP.
                .authorizeHttpRequests(authorize -> authorize
                        // Por enquanto, permite que TODAS as requisições sejam acessadas sem autenticação.
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}