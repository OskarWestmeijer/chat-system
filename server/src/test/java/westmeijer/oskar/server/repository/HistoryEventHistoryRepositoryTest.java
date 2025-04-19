package westmeijer.oskar.server.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import westmeijer.oskar.server.repository.history.ClientMessage;

class HistoryEventHistoryRepositoryTest {

  PublicEventHistoryRepository repo = PublicEventHistoryRepository.getInstance();

  @BeforeEach
  void clearHistory() {
    repo.clearHistory();
  }

  @Test
  void shouldBuildSingletonInstance() {
    var repository1 = PublicEventHistoryRepository.getInstance();
    var repository2 = PublicEventHistoryRepository.getInstance();

    then(repository1).isNotNull();
    then(repository2).isNotNull();

    then(repository1).isSameAs(repository2);
  }

  @Test
  void shouldInitEmptyPublicEventList() {
    var repo = PublicEventHistoryRepository.getInstance();

    then(repo.getHistory()).isEmpty();
  }

  @Test
  void shouldAddPublicEvent() {
    var repo = PublicEventHistoryRepository.getInstance();
    var message = mock(ClientMessage.class);

    repo.insertMessage(message);

    then(repo.getHistory()).containsExactly(message);
  }

}