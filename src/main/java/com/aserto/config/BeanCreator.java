package com.aserto.config;

import com.aserto.AuthorizerClient;
import com.aserto.authorizer.AuthzClient;
import com.aserto.ChannelBuilder;
import com.aserto.directory.v3.DirectoryClient;
import com.aserto.authroizer.config.loader.spring.AuhorizerLoader;
import com.aserto.authroizer.config.loader.spring.DirectoryLoader;
import com.aserto.authroizer.mapper.extractor.Extractor;
import com.aserto.authroizer.mapper.extractor.HeaderExtractor;
import com.aserto.authroizer.mapper.identity.IdentityMapper;
import com.aserto.authroizer.mapper.identity.JwtIdentityMapper;
import com.aserto.store.ResourceStore;
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
    private final DirectoryClient directoryClient;

    public BeanCreator(DirectoryLoader directoryLoader, AuhorizerLoader auhorizerLoader) throws SSLException {
        this.auhorizerLoader = auhorizerLoader;

        ManagedChannel directoryChannel = new ChannelBuilder(directoryLoader.loadConfig()).build();
        this.directoryClient = new DirectoryClient(directoryChannel);
    }

    @Bean
    public AuthorizerClient authorizerClientDiscoverer() throws SSLException {
        ManagedChannel channel = new ChannelBuilder(auhorizerLoader.loadConfig()).build();

        return new AuthzClient(channel);
    }

    @Bean
    public IdentityMapper identityMap() {
        Extractor headerExtractor = new HeaderExtractor("Authorization");
        return new JwtIdentityMapper(headerExtractor);
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
