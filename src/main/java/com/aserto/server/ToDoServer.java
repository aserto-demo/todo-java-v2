package com.aserto.server;

import com.aserto.AuthorizerClient;
import com.aserto.AuthzClient;
import com.aserto.ChannelBuilder;
import com.aserto.DirectoryClient;
import com.sun.net.httpserver.HttpServer;
import io.grpc.ManagedChannel;
import com.aserto.TodoStore;
import com.aserto.EnvConfigLoader;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ToDoServer {
     private ManagedChannel authzChannel;
     private ManagedChannel directoryChannel;
     public ToDoServer() throws IOException {
          EnvConfigLoader envCfgLoader = new EnvConfigLoader();
          authzChannel = new ChannelBuilder(envCfgLoader.getAuthzConfig()).build();
          directoryChannel = new ChannelBuilder(envCfgLoader.getDirectoryConfig()).build();

          AuthorizerClient authzClient = new AuthzClient(authzChannel);
          DirectoryClient directoryClient = new DirectoryClient(directoryChannel);
          TodoStore todoStore = new TodoStore();

          // Create HTTP server
          HttpServer server = HttpServer.create(new InetSocketAddress(3001), 0);
          server.createContext("/todos", new TodosHandler(authzClient, directoryClient, todoStore));
          server.createContext("/user", new UsersHandler(authzClient, directoryClient));

          server.start();
     }

     public void close() {
          authzChannel.shutdown();
          directoryChannel.shutdown();
     }
}
