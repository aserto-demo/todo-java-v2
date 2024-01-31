package com.aserto.store;

import com.aserto.directory.v3.DirectoryClient;
import com.aserto.directory.v3.UninitilizedClientException;
import com.google.protobuf.Struct;

public class ResourceStore {
    private final DirectoryClient directoryClient;

    public ResourceStore(DirectoryClient directoryClient) {
        this.directoryClient = directoryClient;
    }

    public void createResourceForUser(String todoOwnerId, String todoId, String toDoDisplayName) throws UninitilizedClientException {
        directoryClient.setObject("resource", todoId, toDoDisplayName, Struct.newBuilder().build(), "");
        directoryClient.setRelation("resource", todoId, "owner", "user", todoOwnerId, "");
    }

    public void deleteResource(String todoId) throws UninitilizedClientException {
        directoryClient.deleteObject("resource", todoId, true);
    }
}
