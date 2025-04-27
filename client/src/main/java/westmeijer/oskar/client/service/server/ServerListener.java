package westmeijer.oskar.client.service.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.service.StreamProvider;
import westmeijer.oskar.shared.model.response.ServerMessage;

@Slf4j
@RequiredArgsConstructor
public class ServerListener {

  private final StreamProvider streamProvider;
  private final ServerProcessor serverProcessor;

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
