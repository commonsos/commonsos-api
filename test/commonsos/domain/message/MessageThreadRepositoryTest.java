package commonsos.domain.message;

import commonsos.DBTest;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserRepository;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class MessageThreadRepositoryTest extends DBTest {

  UserRepository userRepository = new UserRepository(entityManagerService);
  MessageThreadRepository repository = new MessageThreadRepository(entityManagerService);

  @Test
  public void byAdId() {
    inTransaction(() -> repository.create(new MessageThread().setAdId("10").setCreatedBy("me")));
    inTransaction(() -> repository.create(new MessageThread().setAdId("20").setCreatedBy("other-user")));
    String id = inTransaction(() -> repository.create(new MessageThread().setAdId("20").setCreatedBy("me"))).getId();

    Optional<MessageThread> result = repository.byAdId(new User().setId("me"), "20");
    assertThat(result).isNotEmpty();
    assertThat(result.get().getId()).isEqualTo(id);
  }

  @Test
  public void byAdId_notFound() {
    assertThat(repository.byAdId(new User().setId("me"), "20")).isEmpty();
  }

  @Test
  public void create() {
    User myself = inTransaction(() -> userRepository.create(new User()));
    User counterparty = inTransaction(() -> userRepository.create(new User()));

    MessageThread messageThread = new MessageThread()
      .setParties(asList(myself, counterparty));
    String id = inTransaction(() -> repository.create(messageThread).getId());

    MessageThread result = em().find(MessageThread.class, id);
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();

    assertThat(result.getParties()).containsExactly(myself, counterparty);
  }

  @Test
  public void listByUser() {
    User user = inTransaction(() -> userRepository.create(new User()));
    User otherUser = inTransaction(() -> userRepository.create(new User()));

    MessageThread thread1 = new MessageThread().setParties(asList(user, otherUser));
    MessageThread thread2 = new MessageThread().setParties(asList(otherUser));
    MessageThread thread3 = new MessageThread().setParties(asList(otherUser, user));

    String id1 = inTransaction(() -> repository.create(thread1).getId());
    String id2 = inTransaction(() -> repository.create(thread2).getId());
    String id3 = inTransaction(() -> repository.create(thread3).getId());

    List<MessageThread> result = repository.listByUser(user);

    assertThat(result).extracting("id").containsExactly(id1, id3);
  }

  @Test
  public void threadById() {
    User user = inTransaction(() -> userRepository.create(new User()));
    String id = inTransaction(() -> repository.create(new MessageThread().setParties(asList(user))).getId());

    Optional<MessageThread> result = repository.thread(id);

    assertThat(result).isNotEmpty();
    assertThat(result.get().getId()).isEqualTo(id);
  }

  @Test
  public void threadById_notFound() {
    assertThat(repository.thread("1")).isEmpty();
  }
}