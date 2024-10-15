package com.aserto.config;

import javax.net.ssl.SSLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aserto.AuthorizerClient;
import com.aserto.ChannelBuilder;
import com.aserto.authorizer.AuthzClient;
import com.aserto.authorizer.config.loader.spring.AuthorizerLoader;
import com.aserto.authorizer.config.loader.spring.DirectoryLoader;
import com.aserto.authorizer.mapper.extractor.AuthzHeaderExtractor;
import com.aserto.authorizer.mapper.extractor.Extractor;
import com.aserto.authorizer.mapper.identity.IdentityMapper;
import com.aserto.authorizer.mapper.identity.SubjectIdentityMapper;
import com.aserto.directory.v3.DirectoryClient;
import com.aserto.store.ResourceStore;
import com.aserto.store.UserStore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.grpc.ManagedChannel;

@Configuration
public class BeanCreator {
    private final AuthorizerLoader authorizerLoader;
    private final DirectoryClient directoryClient;

    public BeanCreator(DirectoryLoader directoryLoader, AuthorizerLoader authorizerLoader) throws SSLException {
        this.authorizerLoader = authorizerLoader;

        ManagedChannel directoryChannel = new ChannelBuilder(directoryLoader.loadConfig()).build();
        this.directoryClient = new DirectoryClient(directoryChannel);
    }

    @Bean
    public AuthorizerClient authorizerClientDiscoverer() throws SSLException {
        ManagedChannel channel = new ChannelBuilder(authorizerLoader.loadConfig()).build();

        return new AuthzClient(channel);
    }

    @Bean
    public IdentityMapper identityMap() {
        Extractor headerExtractor = new AuthzHeaderExtractor("Authorization", "sub");
        return new SubjectIdentityMapper(headerExtractor);
    }

    @Bean
    public UserStore createUserStore() {
        return new UserStore(directoryClient);
    }

    @Bean
    public ResourceStore createResourceStore() {
        return new ResourceStore(directoryClient);
    }

    @Bean
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
