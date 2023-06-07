package com.aserto.server;

import com.aserto.AuthorizerClient;
import com.aserto.EnvConfigLoader;
import com.aserto.authorizer.v2.Decision;
import com.aserto.model.IdentityCtx;
import com.aserto.model.PolicyCtx;
import com.google.protobuf.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Authorizer {
    private AuthorizerClient authzClient;
    private Logger logger;

    public Authorizer(AuthorizerClient authzClient) {
        logger = Logger.getLogger(EnvConfigLoader.class.getName());
        this.authzClient = authzClient;
    }

    public boolean isAllowed(IdentityCtx identityCtx, PolicyCtx policyCtx) {
        return isAllowed(identityCtx,policyCtx, Collections.emptyMap());
    }
    public boolean isAllowed(IdentityCtx identityCtx, PolicyCtx policyCtx, Map<String, Value> resourceCtx) {
        List<Decision> decisions = authzClient.is(identityCtx, policyCtx, resourceCtx);

        boolean isDecision = decisions.stream()
                .filter(decision -> decision.getDecision().equals("allowed"))
                .findFirst()
                .get()
                .getIs();

        logger.log(Level.INFO, "Policy [{0}] decision [{1}]", new Object[]{policyCtx.getPath().trim(), isDecision});

        return isDecision;
    }
}
