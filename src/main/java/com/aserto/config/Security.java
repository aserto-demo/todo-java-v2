package com.aserto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import com.aserto.authorizer.AsertoAuthorizationManager;
import com.aserto.authorizer.AuthzConfig;
import com.aserto.authorizer.CheckConfig;

@Configuration
// The @EnableMethodSecurity annotation enables Spring Security's pre/post annotations on the controllers
// @EnableMethodSecurity
public class Security {
    private final AuthzConfig authzCfg;
    public Security(AuthzConfig authzCfg) {
        this.authzCfg = authzCfg;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.DELETE, "/todos/{id}")
                .access(new AsertoAuthorizationManager(authzCfg))

                .requestMatchers(HttpMethod.GET, "/todos")
                .access(new AsertoAuthorizationManager(authzCfg))

                .requestMatchers(HttpMethod.GET, "/users/{userID}")
                .access(new AsertoAuthorizationManager(authzCfg))

                // the POST authorization check can also be written on the controller using method level authorization
                .requestMatchers(HttpMethod.POST, "/todos")
                .access(new CheckConfig(authzCfg, "resource-creator", "resource-creators", "member").getAuthManager())

                .requestMatchers(HttpMethod.PUT, "/todos/{id}")
                .access(new AsertoAuthorizationManager(authzCfg))

                .anyRequest().denyAll()
            );

        return http.build();
    }
}
