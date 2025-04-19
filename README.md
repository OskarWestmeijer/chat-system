# chat-system

[![main-build-test-release](https://github.com/OskarWestmeijer/chat-system/actions/workflows/main-build-test-release.yml/badge.svg?branch=main)](https://github.com/OskarWestmeijer/chat-system/actions/workflows/main-build-test-release.yml)
[![codecov](https://codecov.io/gh/OskarWestmeijer/chat-system/branch/main/graph/badge.svg?token=A03WA88I2Y)](https://codecov.io/gh/OskarWestmeijer/chat-system)

TCP based client-server chat system. The server accepts client socket connections to participate in chat with others. Inspired by an example
from the book "Head First Java".

## Technologies

```
- Java & Gradle Multi-Project builds
- TCP layer socket networking
```

## Local development

Java 21 has to be installed. The project is structured in a multi-project build. The `shared` subproject maintains the serializable
exchanged models.

### Test and build

```
./gradlew clean check
```

### run in IDE

- start `ServerMain.main()`
- start `ClientMain.main()` for as many times as clients are needed
- jump between the terminal sessions and start chatting

### build executables and run

Execute jars in separate terminal tabs.

```
./gradlew clean buildJars
java -jar server/build/libs/chat-system-server.jar
java -jar client/build/libs/chat-system-client.jar
```