package westmeijer.oskar.client.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.thenCode;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.willDoNothing;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import westmeijer.oskar.shared.model.response.ServerMessage;

class StreamProviderTest {

  private Scanner scanner;
  private Socket socket;
  private ObjectInputStream objectInputStream;
  private ObjectOutputStream objectOutputStream;
  private StreamProvider streamProvider;

  @BeforeEach
  void setUp() {
    scanner = mock(Scanner.class);
    socket = mock(Socket.class);
    objectInputStream = mock(ObjectInputStream.class);
    objectOutputStream = mock(ObjectOutputStream.class);
    streamProvider = new StreamProvider(scanner, socket, objectInputStream, objectOutputStream);
  }

  @Test
  void shouldReadFromTerminal() {
    // given
    given(scanner.nextLine()).willReturn("test input");

    // when
    String result = streamProvider.readFromTerminal();

    // then
    then(result).isEqualTo("test input");
  }

  @Test
  void shouldWriteToStream() throws Exception {
    // given
    willDoNothing().given(objectOutputStream).writeObject(any());
    willDoNothing().given(objectOutputStream).flush();

    // when / then
    thenCode(() -> streamProvider.writeToStream("test")).doesNotThrowAnyException();
    BDDMockito.then(objectOutputStream).should().writeObject("test");
    BDDMockito.then(objectOutputStream).should().flush();
  }

  @Test
  void shouldReadFromStream() throws Exception {
    // given
    ServerMessage expectedMessage = mock(ServerMessage.class);
    given(objectInputStream.readObject()).willReturn(expectedMessage);

    // when
    ServerMessage result = streamProvider.readFromStream();

    // then
    then(result).isSameAs(expectedMessage);
  }

  @Test
  void shouldCloseStreams() throws Exception {
    // given
    willDoNothing().given(objectInputStream).close();
    willDoNothing().given(objectOutputStream).close();
    willDoNothing().given(socket).close();

    // when / then
    thenCode(() -> streamProvider.closeStreams()).doesNotThrowAnyException();
  }
}
