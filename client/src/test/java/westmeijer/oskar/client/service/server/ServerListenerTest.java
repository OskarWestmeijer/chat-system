package westmeijer.oskar.client.service.server;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import westmeijer.oskar.client.service.StreamProvider;
import westmeijer.oskar.shared.model.response.ServerMessage;

@ExtendWith(MockitoExtension.class)
class ServerListenerTest {

  @Mock
  private StreamProvider streamProvider;
  @Mock
  private ServerProcessor serverProcessor;
  @Mock
  private ServerMessage serverMessage;

  @InjectMocks
  private ServerListener serverListener;

  @Test
  void shouldReadAndProcessServerMessagesWhileConnected() {
    // Given
    given(streamProvider.isConnected()).willReturn(true, false); // first true, then false to exit loop
    given(streamProvider.readFromStream()).willReturn(serverMessage);

    Runnable runnable = serverListener.runnable();

    // When
    runnable.run();

    // Then
    then(streamProvider).should(times(2)).isConnected();
    then(streamProvider).should().readFromStream();
    then(serverProcessor).should().process(serverMessage);
  }
}
