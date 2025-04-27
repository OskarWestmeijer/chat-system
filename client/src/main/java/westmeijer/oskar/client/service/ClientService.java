package westmeijer.oskar.client.service;


import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import westmeijer.oskar.client.service.server.ServerListener;
import westmeijer.oskar.client.service.terminal.TerminalListener;

@Slf4j
@RequiredArgsConstructor
public class ClientService {

  private final StreamProvider streamProvider;
  private final ExecutorService executorService;
  private final TerminalListener terminalListener;
  private final ServerListener serverListener;

  public void start() {
    try {
      var serverRunnable = serverListener.runnable();
      var terminalRunnable = terminalListener.runnable();
      streamProvider.setConnected(true);
      var serverFuture = executorService.submit(serverRunnable);
      var terminalFuture = executorService.submit(terminalRunnable);
      while (!terminalFuture.isDone() && !serverFuture.isDone()) {
        Thread.sleep(1000);
        log.trace("Still connected." + terminalFuture.isDone() + " " + serverFuture.isDone());
      }
    } catch (Exception e) {
      log.error("Exception received. Disconnecting from server.", e);
      throw new RuntimeException(e);
    } finally {
      stop();
    }
  }

  private void stop() {
    log.info("Disconnecting.");
    streamProvider.setConnected(false);
    streamProvider.closeStreams();
    executorService.shutdownNow();
    streamProvider.exit();
  }

}
