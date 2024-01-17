package com.aserto.config;

import com.aserto.authroizer.AsertoAuthorizationManager;
import com.aserto.authroizer.AuthzConfig;
import com.aserto.authroizer.CheckConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// The @EnableMethodSecurity annotation enables Spring Security's pre/post annotations on the controllers
@EnableMethodSecurity
public class Security {
    private AuthzConfig authzCfg;
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

                // the post authorization can be written here or as an annotation in the controller
                // if we want to have the condition in the controller we permit the traffic to pass here and block it in the controller
                .requestMatchers(HttpMethod.POST, "/todos")
                .permitAll()
//                .access(new CheckConfig(authzCfg, "resource-creator", "resource-creators", "member").getAuthManager())

                .requestMatchers(HttpMethod.PUT, "/todos/{id}")
                .access(new AsertoAuthorizationManager(authzCfg))

                .anyRequest().denyAll()
            );

        return http.build();
    }
}
