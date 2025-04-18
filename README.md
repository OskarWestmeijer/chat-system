# chat-server

Accepts client connections to participate in chat with others. Inspired by an example from the book "Head First Java".

## local development

Java 21 has to be installed.

### test

```
./gradlew clean check
```

### how-to build

```
./gradlew clean shadowJar
java -jar build/libs/chat-server-1.1-all.jar
```