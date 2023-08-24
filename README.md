# todo-java-v2
This application demonstrates how to use [Aserto's Java Spring middleware](https://github.com/aserto-dev/aserto-spring) to add an authorization layer to a simple todo app.

[![slack](https://img.shields.io/badge/slack-Aserto%20Community-brightgreen)](https://asertocommunity.slack.com)

## Setting up the `.env` file
Create the `.env` file in the resources directory
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Update the following values with data for Topaz or for the Aserto hosted authorizer:
```properties
--- Authorizer configuration
aserto.authorizer.host=localhost
aserto.authorizer.port=8282
aserto.authorization.enabled=true
aserto.authorizer.policyRoot=todoApp
aserto.authorizer.decision=allowed

## Topaz
##  This configuration targets a Topaz instance running locally.
aserto.authorizer.insecure=false
aserto.authorizer.grpc.caCertPath=${user.home}/.config/topaz/certs/grpc-ca.crt

## Aserto hosted authorizer
#aserto.tenantId=<tenant_id>
#aserto.authorizer.policyName=todo
#aserto.authorizer.policyLabel=todo
#aserto.authorizer.apiKey=<api_key>



# --- Directory configuration
aserto.directory.host=localhost
aserto.directory.port=9292
aserto.directory.insecure=false

# Topaz directory
aserto.directory.grpc.caCertPath=${user.home}/.config/topaz/certs/grpc-ca.crt

## Aserto hosted directory
#aserto.directory.apiKey=<api_key>
#aserto.directory.token=<auth_token>

# App configuration
logging.level.com.aserto=DEBUG
server.port=3001


```

## Building

```bash
mvn clean package
```

## Running

To run the examples, execute:

```bash
java -jar target/todo-java-v2.jar
```

Run the fallowing commands to test the example:

### Create todo
```bash
curl --location 'localhost:3001/api/todo' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
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
curl --location 'localhost:3001/api/todos' \
--header 'Authorization: Bearer <JWT_TOKEN>'
```

### Update todo
```bash
curl --location --request PUT 'localhost:3001/api/todo/CiRmZDE2MTRkMy1jMzlhLTQ3ODEtYjdiZC04Yjk2ZjVhNTEwMGQSBWxvY2Fs' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
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
curl --location --request DELETE 'localhost:3001/api/todo/CiRmZDE2MTRkMy1jMzlhLTQ3ODEtYjdiZC04Yjk2ZjVhNTEwMGQSBWxvY2Fs' \
--header 'Authorization: Bearer <JWT_TOKEN>' \
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
curl --location 'localhost:3001/api/user/CiRmZDE2MTRkMy1jMzlhLTQ3ODEtYjdiZC04Yjk2ZjVhNTEwMGQSBWxvY2Fs' \
--header 'Authorization: Bearer <JWT_TOKEN>'
```
