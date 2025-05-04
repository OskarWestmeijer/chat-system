package westmeijer.oskar.server.service;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerInitializer {

  @Getter
  private static final ServerInitializer instance = new ServerInitializer();

  public void init(Integer port) {
    Objects.requireNonNull(port, "port is required");
    var clientRegister = ClientRegister.getInstance();
    var historizedEventService = HistorizedEventService.getInstance();
    var clientInitializer = ClientInitializer.getInstance();
    var serverProcessor = ConnectionProcessor.init(historizedEventService, clientInitializer, clientRegister);

    var server = ConnectionListener.serverSocket(port);
    var serverController = ConnectionListener.init(server, serverProcessor);
    serverController.listenForConnection();
  }

}
