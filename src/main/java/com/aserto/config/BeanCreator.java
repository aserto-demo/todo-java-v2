package com.aserto.config;

import com.aserto.AuthorizerClient;
import com.aserto.AuthzClient;
import com.aserto.ChannelBuilder;
import com.aserto.DirectoryClient;
import com.aserto.authroizer.config.loader.spring.AuhorizerLoader;
import com.aserto.authroizer.config.loader.spring.DirectoryLoader;
import com.aserto.store.TodoStore;
import com.aserto.store.UserStore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;

@Configuration
public class BeanCreator {
    private final AuhorizerLoader auhorizerLoader;
    private final DirectoryLoader directoryLoader;

    public BeanCreator(DirectoryLoader directoryLoader, AuhorizerLoader auhorizerLoader) {
        this.directoryLoader = directoryLoader;
        this.auhorizerLoader = auhorizerLoader;
    }

    @Bean
    public AuthorizerClient authorizerClientDiscoverer() throws SSLException {
        ManagedChannel channel = new ChannelBuilder(auhorizerLoader.loadConfig()).build();

        return new AuthzClient(channel);
    }


    @Bean
    public UserStore createUserStore() throws SSLException {
        ManagedChannel directoryChannel = new ChannelBuilder(directoryLoader.loadConfig()).build();
        DirectoryClient directoryClient = new DirectoryClient(directoryChannel);

        return new UserStore(directoryClient);
    }

    @Bean
    public TodoStore createTodoStore() {
        return new TodoStore();
    }

    @Bean
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}