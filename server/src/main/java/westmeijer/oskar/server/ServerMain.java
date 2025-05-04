package westmeijer.oskar.server;

import java.io.IOException;
import java.net.ServerSocket;
import westmeijer.oskar.server.service.ClientInitializer;
import westmeijer.oskar.server.service.ClientRegister;
import westmeijer.oskar.server.service.ConnectionListener;
import westmeijer.oskar.server.service.HistorizedEventService;
import westmeijer.oskar.server.service.ConnectionProcessor;

public class ServerMain {

  public static void main(String[] args) throws IOException {
    var clientRegister = ClientRegister.getInstance();
    var historizedEventService = HistorizedEventService.getInstance();
    var clientInitializer = new ClientInitializer();
    var serverProcessor = new ConnectionProcessor(historizedEventService, clientInitializer, clientRegister);

    var server = new ServerSocket(5123);
    var serverController = new ConnectionListener(server, serverProcessor);
    serverController.listenForConnection();
  }

  public static boolean isListening() {
    return true;
  }

}
