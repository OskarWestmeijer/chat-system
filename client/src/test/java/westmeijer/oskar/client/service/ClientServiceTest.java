package westmeijer.oskar.client.service;


import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import westmeijer.oskar.client.service.server.ServerListener;
import westmeijer.oskar.client.service.terminal.TerminalListener;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

  @Mock
  private StreamProvider streamProvider;
  @Mock
  private ExecutorService executorService;
  @Mock
  private TerminalListener terminalListener;
  @Mock
  private ServerListener serverListener;
  @Mock
  private Runnable terminalRunnable;
  @Mock
  private Runnable serverRunnable;
  @Mock
  private Future terminalFuture;
  @Mock
  private Future serverFuture;

  private ClientService clientService;

  @BeforeEach
  void setUp() {
    clientService = new ClientService(streamProvider, executorService, terminalListener, serverListener);
  }

  @Test
  @SneakyThrows
  void shouldStartAndStopSuccessfully() {
    // Given
    given(serverListener.runnable()).willReturn(serverRunnable);
    given(terminalListener.runnable()).willReturn(terminalRunnable);
    given(executorService.submit(serverRunnable)).willReturn(serverFuture);
    given(executorService.submit(terminalRunnable)).willReturn(terminalFuture);
    given(terminalFuture.isDone()).willReturn(false, true); // First check false, second true
    given(serverFuture.isDone()).willReturn(false, true);

    // When
    clientService.start();

    // Then
    then(streamProvider).should().setConnected(true);
    then(executorService).should().submit(serverRunnable);
    then(executorService).should().submit(terminalRunnable);
    then(streamProvider).should().setConnected(false);
    then(streamProvider).should().closeStreams();
    then(executorService).should().shutdownNow();
    then(streamProvider).should().exit();
  }

  @Test
  @SneakyThrows
  void shouldStopAndThrowWhenExceptionOccurs() {
    // Given
    given(serverListener.runnable()).willThrow(new RuntimeException("Test Exception"));

    // When / Then
    thenThrownBy(() -> clientService.start())
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(RuntimeException.class)
        .hasRootCauseMessage("Test Exception");

    then(streamProvider).should().setConnected(false);
    then(streamProvider).should().closeStreams();
    then(executorService).should().shutdownNow();
    then(streamProvider).should().exit();
  }
}