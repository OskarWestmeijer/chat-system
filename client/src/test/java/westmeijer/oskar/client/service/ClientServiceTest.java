package westmeijer.oskar.client.service;

import static org.assertj.core.api.BDDAssertions.thenNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.io.ObjectOutputStream;
import java.util.Scanner;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import westmeijer.oskar.shared.model.request.ClientChatRequest;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

  @Mock
  private ServerListener serverListener;

  @Mock
  private Scanner scanner;

  @InjectMocks
  private ClientService clientService;

  @Test
  void shouldNotScanTerminalIfNoConnection() {
    given(serverListener.isConnected()).willReturn(false);

    clientService.start();

    BDDMockito.then(serverListener).should().connect();
    BDDMockito.then(serverListener).should().isConnected();
    BDDMockito.then(scanner).should(never()).nextLine();
    BDDMockito.then(scanner).should().close();
    BDDMockito.then(serverListener).should().disconnect();
  }

  @Test
  void shouldReceiveMessageAndStop() {
    given(serverListener.isConnected())
        .willReturn(true)
        .willReturn(false);

    var stream = mock(ObjectOutputStream.class);
    given(serverListener.getObjectOutputStream()).willReturn(stream);

    String chatMessage = "Hey";
    given(scanner.nextLine()).willReturn(chatMessage);

    clientService.start();

    BDDMockito.then(serverListener).should().connect();
    BDDMockito.then(serverListener).should(times(2)).isConnected();
    BDDMockito.then(serverListener).should().getObjectOutputStream();
    BDDMockito.then(scanner).should().nextLine();
    BDDMockito.then(scanner).should().close();
    BDDMockito.then(serverListener).should().disconnect();
  }

  @Test
  void shouldHandleConnectionException() {
    // Given
    doThrow(new RuntimeException("Connection error")).when(serverListener).connect();

    // When
    thenNoException().isThrownBy(() -> clientService.start());

    // Then
    BDDMockito.then(scanner).should().close();
    BDDMockito.then(serverListener).should().disconnect();
  }

  @Test
  @SneakyThrows
  void shouldSendChatMessageCorrectly() {
    // Given
    given(serverListener.isConnected()).willReturn(true).willReturn(false);
    var stream = mock(ObjectOutputStream.class);
    given(serverListener.getObjectOutputStream()).willReturn(stream);

    String chatMessage = "Hello Server!";
    given(scanner.nextLine()).willReturn(chatMessage);

    // When
    clientService.start();

    // Then
    BDDMockito.then(serverListener).should().connect();
    BDDMockito.then(scanner).should().nextLine();
    BDDMockito.then(serverListener).should().getObjectOutputStream();
    BDDMockito.then(stream).should().writeObject(any(ClientChatRequest.class)); // Assuming the message gets wrapped in the request
    BDDMockito.then(scanner).should().close();
    BDDMockito.then(serverListener).should().disconnect();
  }

  @Test
  void shouldDisconnectWhenQuitCommandEntered() {
    // Given
    given(serverListener.isConnected()).willReturn(true).willReturn(false);
    given(scanner.nextLine()).willReturn("/quit");

    // When
    clientService.start();

    // Then
    BDDMockito.then(scanner).should().nextLine();
    BDDMockito.then(scanner).should().close();
    BDDMockito.then(serverListener).should().disconnect();
  }

}