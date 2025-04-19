# chat-system

TCP based client-server chat system. The server accepts client socket connections to participate in chat with others. Inspired by an example
from the book "Head First Java".

## local development

Java 21 has to be installed. The project is structured in a multi-project build. The `shared` subproject maintains the serializable
exchanged models.

### test

```
./gradlew clean check
```

### run in IDE

- start `ServerMain.main()`
- start `ClientMain.main()` for as many times as clients are needed
- jump between the terminal sessions and start chatting

### build executables and run

```
./gradlew clean buildJars
java -jar server/build/libs/chat-system-server.jar
java -jar client/build/libs/chat-system-client.jar
```