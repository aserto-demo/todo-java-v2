package com.aserto.config;

import com.aserto.authroizer.AsertoAuthorizationManager;
import com.aserto.authroizer.AuthzConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class Security {
    private AuthzConfig authzCfg;
    public Security(AuthzConfig authzCfg) {
        this.authzCfg = authzCfg;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(authorize -> authorize
                .anyRequest().access(new AsertoAuthorizationManager(authzCfg))
        );

        return http.build();
    }
}
