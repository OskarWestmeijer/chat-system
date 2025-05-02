package westmeijer.oskar.client.service.server;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.service.StreamProvider;
import westmeijer.oskar.shared.model.response.ServerMessage;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerListener {

  private final StreamProvider streamProvider;
  private final ServerProcessor serverProcessor;
  private static ServerListener instance;

  public static ServerListener init(StreamProvider streamProvider, ServerProcessor serverProcessor) {
    if (instance == null) {
      instance = new ServerListener(streamProvider, serverProcessor);
    }
    return instance;
  }

  public Runnable runnable() {
    // TODO: when server shuts down, this application does not. Control with future?
    return () -> {
      while (streamProvider.isConnected()) {
        ServerMessage serverMessage = streamProvider.readFromStream();
        serverProcessor.process(serverMessage);
      }
    };
  }

}
