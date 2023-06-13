# todo-java-v2
This application demonstrates how to use [Aserto's Java SDK](https://github.com/aserto-dev/aserto-java) to add an authorization layer to a simple todo app.

[![slack](https://img.shields.io/badge/slack-Aserto%20Community-brightgreen)](https://asertocommunity.slack.com)

## Setting up the `.env` file
Create the `.env` file in the resources directory
```bash
cp src/main/resources/.env.example src/main/resources/.env
```

Update the fallowing values with data for Topaz or for the Aserto hosted authorizer:
```
JWKS_URI=https://citadel.demo.aserto.com/dex/keys
ISSUER=https://citadel.demo.aserto.com/dex
AUDIENCE=citadel-app

ASERTO_POLICY_ROOT="todoApp"

# Topaz
#
# This configuration targets a Topaz instance running locally.
# To target an Aserto hosted authorizer, comment out the lines below and uncomment the section
# at the bottom of this file.
ASERTO_AUTHORIZER_SERVICE_URL=localhost:8282
ASERTO_AUTHORIZER_CERT_PATH=$HOME/.config/topaz/certs/grpc-ca.crt
ASERTO_DIRECTORY_SERVICE_URL=localhost:9292
ASERTO_DIRECTORY_GRPC_CERT_PATH=$HOME/.config/topaz/certs/grpc-ca.crt

# Aserto hosted authorizer
#
# To run the server using an Aserto hosted authorizer, the following variables are required:
# ASERTO_AUTHORIZER_SERVICE_URL=authorizer.prod.aserto.com:8443
# ASERTO_DIRECTORY_SERVICE_URL=directory.prod.aserto.com:8443
# ASERTO_TENANT_ID={Your Aserto Tenant ID UUID}
# ASERTO_AUTHORIZER_API_KEY={Your Authorizer API Key}
# ASERTO_DIRECTORY_API_KEY={Your Directory (read-only) API Key}
# ASERTO_POLICY_INSTANCE_NAME=todo
# ASERTO_POLICY_INSTANCE_LABEL=todo

```

## Building

```bash
mvn clean package
```

## Running

To run the examples, execute:

```bash
java -jar target/todo-java-v2-0.0.1-shaded.jar
```

Run the fallowing commands to test the example:

### Create todo
```bash
curl --location 'localhost:8500/api/todo' \
--header 'Authorization: Beare <JWT_TOKEN>' \
--header 'Content-Type: application/json' \
--data '{
    "ID": "id-test",
    "Title": "todo-test",
    "Completed": false,
    "OwnerID": "sub-test"
}'
```

### Get todos
```bash
curl --location 'localhost:8500/api/todos' \
--header 'Authorization: Beare <JWT_TOKEN>'
```

### Update todo
```bash
curl --location --request PUT 'localhost:8500/api/todo/CiRmZDE2MTRkMy1jMzlhLTQ3ODEtYjdiZC04Yjk2ZjVhNTEwMGQSBWxvY2Fs' \
--header 'Authorization: Beare <JWT_TOKEN>' \
--header 'Content-Type: application/json' \
--data '{
    "ID": "id-test",
    "Title": "todo-test",
    "Completed": true,
    "OwnerID": "sub-test"
}'
```

### Delete todo
```bash
curl --location --request DELETE 'localhost:8500/api/todo/CiRmZDE2MTRkMy1jMzlhLTQ3ODEtYjdiZC04Yjk2ZjVhNTEwMGQSBWxvY2Fs' \
--header 'Authorization: Beare <JWT_TOKEN>' \
--header 'Content-Type: application/json' \
--data '{
    "ID": "id-test",
    "Title": "todo-test",
    "Completed": true,
    "OwnerID": "sub-test"
}'
```

### Get user
```bash
curl --location 'localhost:8500/api/user/CiRmZDE2MTRkMy1jMzlhLTQ3ODEtYjdiZC04Yjk2ZjVhNTEwMGQSBWxvY2Fs' \
--header 'Authorization: Beare <JWT_TOKEN>'
```
