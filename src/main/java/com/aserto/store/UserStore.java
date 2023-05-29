package com.aserto.store;

import com.aserto.DirectoryClient;
import com.aserto.directory.common.v2.Object;
import com.aserto.directory.common.v2.ObjectIdentifier;
import com.aserto.directory.common.v2.Relation;
import com.aserto.directory.common.v2.RelationIdentifier;
import com.aserto.directory.common.v2.RelationTypeIdentifier;
import com.aserto.directory.reader.v2.GetObjectRequest;
import com.aserto.directory.reader.v2.GetObjectResponse;
import com.aserto.directory.reader.v2.GetRelationRequest;
import com.aserto.directory.reader.v2.GetRelationResponse;

public class UserStore {
    private DirectoryClient directoryClient;
    public UserStore(DirectoryClient directoryClient) {
        this.directoryClient = directoryClient;
    }

    public Object getUserBySub(String sub) {
        Relation[] relations = getRelationFromIdentityToUser(sub);
        Object object = null;

        switch (relations.length) {
            case 0:
                throw new RuntimeException("No relations found");
            case 1:
                object = getSubjectFromRelation(relations[0]);
                break;
            default:
                throw new RuntimeException("Too many relations found");
        }

        if (relations.length > 1) {
            throw new RuntimeException("Too many relations found");
        }

        return object;
    }

    public Object getUserByKey(String key) {
        ObjectIdentifier objectIdentifier = ObjectIdentifier.newBuilder().setKey(key).setType("user").build();
        GetObjectRequest.Builder builder = GetObjectRequest.newBuilder();
        builder.setParam(objectIdentifier);
        GetObjectRequest request = builder.build();
        GetObjectResponse response = directoryClient.getReaderClient().getObject(request);

        return response.getResult();
    }

    private Relation[] getRelationFromIdentityToUser(String key) {
        ObjectIdentifier subjectIdentifier = ObjectIdentifier.newBuilder().setType("user").build();
        ObjectIdentifier objectIdentifier = ObjectIdentifier.newBuilder().setKey(key).setType("identity").build();
        RelationTypeIdentifier relationTypeIdentifier = RelationTypeIdentifier.newBuilder().setName("identifier").setObjectType("identity").build();

        RelationIdentifier relationIdentifier = RelationIdentifier.newBuilder()
                .setSubject(subjectIdentifier)
                .setObject(objectIdentifier)
                .setRelation(relationTypeIdentifier).build();

        GetRelationRequest.Builder builder = GetRelationRequest.newBuilder();
        builder.setParam(relationIdentifier);
        GetRelationRequest request = builder.build();
        GetRelationResponse response = directoryClient.getReaderClient().getRelation(request);

        return response.getResultsList().toArray(new Relation[0]);
    }

    private Object getSubjectFromRelation(Relation relation) {
        ObjectIdentifier objectIdentifier = relation.getSubject();
        GetObjectRequest.Builder objectBuilder = GetObjectRequest.newBuilder();
        objectBuilder.setParam(objectIdentifier);
        GetObjectRequest objectRequest = objectBuilder.build();
        GetObjectResponse objectResponse = directoryClient.getReaderClient().getObject(objectRequest);

        return objectResponse.getResult();
    }
}
