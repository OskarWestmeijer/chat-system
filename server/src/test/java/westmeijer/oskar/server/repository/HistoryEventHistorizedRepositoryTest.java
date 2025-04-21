package westmeijer.oskar.server.repository;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import westmeijer.oskar.server.service.model.ClientMessage;
import westmeijer.oskar.server.service.HistorizedEventService;

class HistoryEventHistorizedRepositoryTest {

  HistorizedEventService repo = HistorizedEventService.getInstance();

  @BeforeEach
  void clearHistory() {
    repo.clearHistory();
  }

  @Test
  void shouldBuildSingletonInstance() {
    var repository1 = HistorizedEventService.getInstance();
    var repository2 = HistorizedEventService.getInstance();

    then(repository1).isNotNull();
    then(repository2).isNotNull();

    then(repository1).isSameAs(repository2);
  }

  @Test
  void shouldInitEmptyPublicEventList() {
    var repo = HistorizedEventService.getInstance();

    then(repo.getHistory()).isEmpty();
  }

  @Test
  void shouldAddPublicEvent() {
    var repo = HistorizedEventService.getInstance();
    var message = mock(ClientMessage.class);

    repo.recordMessage(message);

    then(repo.getHistory()).containsExactly(message);
  }

}