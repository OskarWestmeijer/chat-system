package westmeijer.oskar.client.service.terminal;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mockStatic;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import westmeijer.oskar.client.loggers.ServerLogger;
import westmeijer.oskar.client.service.StreamProvider;

@ExtendWith(MockitoExtension.class)
class TerminalListenerTest {

  @Mock
  private StreamProvider streamProvider;

  @Mock
  private TerminalProcessor terminalProcessor;

  private TerminalListener terminalListener = TerminalListener.init(streamProvider, terminalProcessor);

  @BeforeEach
  void setup() {
    TerminalListener.reset();
    terminalListener = TerminalListener.init(streamProvider, terminalProcessor);
  }

  @Test
  void shouldReadAndProcessTerminalInputsWhileConnected() {
    // Given
    given(streamProvider.isConnected()).willReturn(true, false); // First true (loop runs), then false (exit)
    given(streamProvider.readFromTerminal()).willReturn("Hello");

    try (MockedStatic<ServerLogger> serverLoggerMock = mockStatic(ServerLogger.class)) {
      Runnable runnable = terminalListener.runnable();

      // When
      runnable.run();

      // Then
      serverLoggerMock.verify(() -> ServerLogger.log("Start chatting. available commands: '/clients', '/history', '/quit'"));
      then(streamProvider).should(times(2)).isConnected();
      then(streamProvider).should().readFromTerminal();
      then(terminalProcessor).should().process("Hello");
    }
  }

  @Test
  void shouldNotProcessInputWhenNotConnected() {
    // Given
    given(streamProvider.isConnected()).willReturn(false); // Immediately disconnected

    try (MockedStatic<ServerLogger> serverLoggerMock = mockStatic(ServerLogger.class)) {
      Runnable runnable = terminalListener.runnable();

      // When
      runnable.run();

      // Then
      serverLoggerMock.verify(() -> ServerLogger.log("Start chatting. available commands: '/clients', '/history', '/quit'"));
      then(streamProvider).should().isConnected();
      then(streamProvider).should(never()).readFromTerminal();
      then(terminalProcessor).shouldHaveNoInteractions();
    }
  }
}
