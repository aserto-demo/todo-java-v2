# todo-java-v2
This application demonstrates how to use [Aserto's Java Spring middleware](https://github.com/aserto-dev/aserto-spring) to add an authorization layer to a simple todo app.

[![slack](https://img.shields.io/badge/slack-Aserto%20Community-brightgreen)](https://asertocommunity.slack.com)


## Configuration

Create an `application.properties` file in the resources directory:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Update the following values with data for Topaz or for the Aserto hosted authorizer:

```properties
# --- Authorizer configuration
aserto.authorization.enabled=true
aserto.authorizer.policyRoot=todoApp
aserto.authorizer.decision=allowed
aserto.authorizer.insecure=false
aserto.directory.insecure=false

## Topaz
##  This configuration targets a Topaz instance running locally.
##  To use Aserto's hosted services, comment out the lines below and uncomment
##  the configuration under the "Aserto hosted environment" section.
aserto.authorizer.serviceUrl=localhost:8282
aserto.authorizer.grpc.caCertPath=${user.home}/.local/share/topaz/certs/grpc-ca.crt
aserto.directory.grpc.caCertPath=${user.home}/.local/share/topaz/certs/grpc-ca.crt

## Aserto hosted environment
##  To run the application against Aserto's hosted environment, uncomment
##  the lines below and:
##    1. Replace <tenant_id> with your own tenant ID.
##    2. Replace <authorizer_key> with your authorizer API key.
##    2. Replace <directory_key> with your directory API key.
##
##  You can find your tenant ID and API keys in the Aserto console (https://console.aserto.com).
##  Navigate to the Policies section, select the 'todo' policy, and go to 'Settings'.
##
# aserto.authorizer.serviceUrl=authorizer.prod.aserto.com:8443
# aserto.directory.serviceUrl=localhost:9292
# aserto.tenantId=<tenant_id>
# aserto.authorizer.policyName=todo
# aserto.authorizer.policyLabel=todo
# aserto.authorizer.apiKey=<authorizer_key>
# aserto.directory.apiKey=<directory_key>

# App configuration
logging.level.com.aserto=DEBUG
server.port=3001

# Create the schema on startup
spring.jpa.hibernate.ddl-auto=update
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
