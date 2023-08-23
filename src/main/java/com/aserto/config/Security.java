package com.aserto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class Security {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf(AbstractHttpConfigurer::disable)
//                .antMatcher("{*path}")
//            .addFilterAfter(asertoFilter, RequestHeaderAuthenticationFilter.class)
//                .authorizeHttpRequests(authz -> authz
//                        .anyRequest().permitAll()
//                )
//                .httpBasic(withDefaults())
        ;

        return http.build();
    }
}
