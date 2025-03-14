package com.aserto.store;

import com.aserto.directory.v3.DirectoryClient;
import com.aserto.directory.common.v3.Object;
import com.aserto.directory.common.v3.Relation;
import com.aserto.directory.reader.v3.GetObjectResponse;
import com.aserto.directory.reader.v3.GetRelationResponse;
import com.aserto.directory.v3.UninitilizedClientException;
import io.grpc.StatusRuntimeException;
import io.grpc.Status;

public class UserStore {
    private DirectoryClient directoryClient;
    private boolean legacy;

    public UserStore(DirectoryClient directoryClient) throws UninitilizedClientException {
        this.directoryClient = directoryClient;
        this.legacy = isLegacy(directoryClient);
    }

    public Object getUserBySub(String id) throws UninitilizedClientException {
        if (this.legacy) {
            return tryResolveLegacy(id);
        }

        return tryResolve(id);
    }

    public Object getUserByID(String id) throws UninitilizedClientException {
        GetObjectResponse response = directoryClient.getObject("user", id);

        return response.getResult();
    }

    private static boolean isLegacy(DirectoryClient directoryClient) throws UninitilizedClientException {
        try {
            directoryClient.getRelation("identity", "todoDemoIdentity", "identifier", "user", "todoDemoUser");
            return true;
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.INVALID_ARGUMENT) {
                // There is no identity#identifier relation. We're using new style identities.
                return false;
            }
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                // The relation doesn't exist but the types are valid. The model uses legacy
                // identities.
                return true;
            }
            throw e;
        }
    }

    private Object tryResolve(String id) throws UninitilizedClientException {
        try {
            GetRelationResponse response = directoryClient.getRelation("user", "", "identifier", "identity", id, true);
            Relation relation = response.getResult();
            String userID = relation.getObjectId();
            return response.getObjectsMap().getOrDefault("user:" + userID, null);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    private Object tryResolveLegacy(String id) throws UninitilizedClientException {
        try {
            GetRelationResponse response = directoryClient.getRelation("identity", id, "identifier", "user", "", true);
            Relation relation = response.getResult();
            String userID = relation.getSubjectId();
            return response.getObjectsMap().getOrDefault("user:" + userID, null);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }
}
