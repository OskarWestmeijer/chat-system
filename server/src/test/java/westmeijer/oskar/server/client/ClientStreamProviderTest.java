package westmeijer.oskar.server.client;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientStreamProviderTest {

  private Socket socket;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private ClientStreamProvider streamProvider;

  @BeforeEach
  void setUp() {
    socket = mock(Socket.class);
    inputStream = mock(ObjectInputStream.class);
    outputStream = mock(ObjectOutputStream.class);
    streamProvider = new ClientStreamProvider(socket, inputStream, outputStream);
  }

  @Test
  void shouldWriteToStream() throws Exception {
    var message = "test-message";

    streamProvider.writeToStream(message);

    verify(outputStream).writeObject(message);
    verify(outputStream).flush();
  }

  @Test
  void shouldReadFromStream() throws Exception {
    var expected = "incoming-message";
    given(inputStream.readObject()).willReturn(expected);

    var result = streamProvider.readFromStream();

    then(result).isEqualTo(expected);
  }

  @Test
  void shouldCloseStreams() throws Exception {
    streamProvider.closeStreams();

    verify(inputStream).close();
    verify(outputStream).close();
    verify(socket).close();
  }

  @Test
  void shouldCreateOutput() throws Exception {
    var socket = mock(Socket.class);
    var byteOut = new java.io.ByteArrayOutputStream();

    when(socket.getOutputStream()).thenReturn(byteOut);

    var result = ClientStreamProvider.createOutput(socket);

    then(result).isNotNull();
    result.writeObject("test");  // optional write test
    result.flush();
  }

}
