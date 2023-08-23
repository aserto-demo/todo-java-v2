package com.aserto.config;

import com.aserto.ChannelBuilder;
import com.aserto.DirectoryClient;
import com.aserto.EnvConfigLoader;
import com.aserto.store.UserStore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import javax.net.ssl.SSLException;

@Configuration
public class Security {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public UserStore createUserStore() throws SSLException {
        EnvConfigLoader envCfgLoader = new EnvConfigLoader();
        ManagedChannel directoryChannel = new ChannelBuilder(envCfgLoader.getDirectoryConfig()).build();
        DirectoryClient directoryClient = new DirectoryClient(directoryChannel);
        return new UserStore(directoryClient);
    }

    @Bean
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
