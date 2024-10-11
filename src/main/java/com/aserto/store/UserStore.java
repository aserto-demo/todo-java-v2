package com.aserto.store;

import com.aserto.directory.v3.DirectoryClient;
import com.aserto.directory.common.v3.Object;
import com.aserto.directory.common.v3.Relation;
import com.aserto.directory.reader.v3.GetObjectResponse;
import com.aserto.directory.reader.v3.GetRelationResponse;
import com.aserto.directory.v3.UninitilizedClientException;

public class UserStore {
    private DirectoryClient directoryClient;
    public UserStore(DirectoryClient directoryClient) {
        this.directoryClient = directoryClient;
    }

    public Object getUserBySub(String id) throws UninitilizedClientException {
        GetRelationResponse response = directoryClient.getRelation("identity", id, "identifier", "user", "");
        Relation relation = response.getResult();
        GetObjectResponse objectResponse = directoryClient.getObject(relation.getSubjectType(), relation.getSubjectId());

        return objectResponse.getResult();
    }

    public Object getUserByID(String id) throws UninitilizedClientException {
        GetObjectResponse response = directoryClient.getObject("user", id);

        return response.getResult();
    }
}
