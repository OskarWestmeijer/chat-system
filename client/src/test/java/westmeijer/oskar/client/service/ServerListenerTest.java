package westmeijer.oskar.client.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import lombok.SneakyThrows;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServerListenerTest {

  @Mock
  private Socket socket;

  @Mock
  private ObjectInputStream objectInputStream;

  @Mock
  private ObjectOutputStream objectOutputStream;

  @InjectMocks
  private ServerListener serverListener;

  @Test
  @SneakyThrows
  void testDisconnectClosesResources() {
    serverListener.disconnect();
    BDDAssertions.then(serverListener.isConnected()).isFalse();
  }

  @Test
  @SneakyThrows
  void testConnectSubmitsTask() {
    ExecutorService mockExecutor = mock(ExecutorService.class);
    var executorField = ServerListener.class.getDeclaredField("executorService");
    executorField.setAccessible(true);
    executorField.set(serverListener, mockExecutor);

    serverListener.connect();

    then(mockExecutor).should().submit(any(Runnable.class));
    BDDAssertions.then(serverListener.isConnected()).isTrue();
  }


}