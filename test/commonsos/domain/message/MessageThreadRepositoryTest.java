package commonsos.domain.message;

import commonsos.DBTest;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserRepository;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static commonsos.TestId.id;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class MessageThreadRepositoryTest extends DBTest {

  UserRepository userRepository = new UserRepository(entityManagerService);
  MessageThreadRepository repository = new MessageThreadRepository(entityManagerService);

  @Test
  public void byAdId() {
    inTransaction(() -> repository.create(new MessageThread().setAdId(10L).setCreatedBy(id("me"))));
    inTransaction(() -> repository.create(new MessageThread().setAdId(20L).setCreatedBy(id("other-user"))));
    Long id = inTransaction(() -> repository.create(new MessageThread().setAdId(20L).setCreatedBy(id("me")))).getId();

    Optional<MessageThread> result = repository.byAdId(new User().setId(id("me")), 20L);
    assertThat(result).isNotEmpty();
    assertThat(result.get().getId()).isEqualTo(id);
  }

  @Test
  public void byAdId_notFound() {
    assertThat(repository.byAdId(new User().setId(id("me")), 20L)).isEmpty();
  }

  @Test
  public void create() {
    User myself = inTransaction(() -> userRepository.create(new User()));
    User counterparty = inTransaction(() -> userRepository.create(new User()));

    List<MessageThreadParty> parties = asList(party(myself), party(counterparty));
    MessageThread messageThread = new MessageThread().setParties(parties);
    Long id = inTransaction(() -> repository.create(messageThread).getId());

    MessageThread result = em().find(MessageThread.class, id);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();

    MessageThreadParty party1 = result.getParties().get(0);
    MessageThreadParty party2 = result.getParties().get(1);

    assertThat(party1.getUser()).isEqualTo(myself);
    assertThat(party1.getVisitedAt()).isNull();

    assertThat(party2.getUser()).isEqualTo(counterparty);
    assertThat(party2.getVisitedAt()).isNull();
  }

  private MessageThreadParty party(User myself) {
    return new MessageThreadParty().setUser(myself);
  }

  @Test
  public void listByUser() {
    User user = inTransaction(() -> userRepository.create(new User()));
    User otherUser = inTransaction(() -> userRepository.create(new User()));

    MessageThread thread1 = new MessageThread().setParties(asList(party(user), party(otherUser)));
    MessageThread thread2 = new MessageThread().setParties(asList(party(otherUser)));
    MessageThread thread3 = new MessageThread().setParties(asList(party(otherUser), party(user)));

    Long id1 = inTransaction(() -> repository.create(thread1).getId());
    Long id2 = inTransaction(() -> repository.create(thread2).getId());
    Long id3 = inTransaction(() -> repository.create(thread3).getId());

    List<MessageThread> result = repository.listByUser(user);

    assertThat(result).extracting("id").containsExactly(id1, id3);
  }

  @Test
  public void threadById() {
    User user = inTransaction(() -> userRepository.create(new User()));
    Long id = inTransaction(() -> repository.create(new MessageThread().setParties(asList(party(user)))).getId());

    Optional<MessageThread> result = repository.thread(id);

    assertThat(result).isNotEmpty();
    assertThat(result.get().getId()).isEqualTo(id);
  }

  @Test
  public void threadById_notFound() {
    assertThat(repository.thread(1L)).isEmpty();
  }

  @Test
  public void updateParty() {
    User user = inTransaction(() -> userRepository.create(new User()));
    MessageThreadParty party = new MessageThreadParty().setUser(user);
    MessageThread thread = new MessageThread().setParties(asList(party));
    inTransaction(() -> repository.create(thread).getId());

    party.setVisitedAt(now());
    inTransaction(() -> repository.update(party));

    MessageThreadParty actual = em().find(MessageThreadParty.class, party.getId());
    assertThat(actual.getVisitedAt()).isCloseTo(now(), within(1, SECONDS));
  }
}