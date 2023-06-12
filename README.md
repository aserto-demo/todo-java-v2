# todo-java-v2
This application demonstrates how to use [Aserto's Java SDK](https://github.com/aserto-dev/aserto-java) to add an authorization layer to a simple todo app.

[![slack](https://img.shields.io/badge/slack-Aserto%20Community-brightgreen)](https://asertocommunity.slack.com)

## Setting up the `.env` file
Create the `.env` file in the resources directory
```bash
cp src/main/resources/.env.example src/main/resources/.env
```

Update the fallowing values with data from the Aserto console:
```
ASERTO_TENANT_ID={Your Aserto Tenant ID UUID}
ASERTO_AUTHORIZER_API_KEY={Your Authorizer API Key}
ASERTO_DIRECTORY_API_KEY={Your Directory (read-only) API Key}
ASERTO_POLICY_INSTANCE_NAME=todo
ASERTO_POLICY_INSTANCE_LABEL=todo
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
