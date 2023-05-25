package com.aserto.server;

import com.aserto.ChannelBuilder;
import com.aserto.DirectoryClient;
import com.aserto.store.TodoStore;
import com.sun.net.httpserver.HttpServer;
import io.grpc.ManagedChannel;
import com.aserto.EnvConfigLoader;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ToDoServer {
     private DirectoryClient directoryClient;
     public ToDoServer() throws IOException {
          EnvConfigLoader envCfgLoader = new EnvConfigLoader();
          ManagedChannel directoryChannel = new ChannelBuilder(envCfgLoader.getDirectoryConfig()).build();

          directoryClient = new DirectoryClient(directoryChannel);
          TodoStore todoStore = new TodoStore();

          // Create HTTP server
          HttpServer server = HttpServer.create(new InetSocketAddress(3001), 0);
          server.createContext("/todos", new TodosHandler(todoStore));
          server.createContext("/user", new UsersHandler(directoryClient));

          server.start();
     }

     public void close() {
          directoryClient.close();
     }
}
