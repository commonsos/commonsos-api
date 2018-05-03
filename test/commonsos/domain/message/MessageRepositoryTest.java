package commonsos.domain.message;

import commonsos.DBTest;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;

public class MessageRepositoryTest extends DBTest {

  private MessageRepository repository = new MessageRepository(entityManagerService);

  @Test
  public void createMessage() {
    Instant now = now();
    Message message = new Message()
      .setCreatedBy("created by")
      .setCreatedAt(now)
      .setText("message text")
      .setThreadId("thread id");

    String id = inTransaction(() -> repository.create(message).getId());

    Message result = em().find(Message.class, id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getCreatedBy()).isEqualTo("created by");
    assertThat(result.getCreatedAt()).isEqualTo(now);
    assertThat(result.getText()).isEqualTo("message text");
    assertThat(result.getThreadId()).isEqualTo("thread id");
  }

  @Test
  public void listByThread_olderMessagesFirst() {
    String id1 = inTransaction(() -> repository.create(new Message().setThreadId("thread id").setCreatedAt(now().minus(1, HOURS))).getId());
    String id2 = inTransaction(() -> repository.create(new Message().setThreadId("thread id").setCreatedAt(now())).getId());
    String id3 = inTransaction(() -> repository.create(new Message().setThreadId("thread id").setCreatedAt(now().minus(2, HOURS))).getId());
    inTransaction(() -> repository.create(new Message().setThreadId("other thread")));

    List<Message> result = repository.listByThread("thread id");

    assertThat(result).extracting("id").containsExactly(id3, id1, id2);
  }

  @Test
  public void lastThreadMessage() {
    Message oldestMessage = new Message().setThreadId("thread id").setCreatedAt(now().minus(2, HOURS));
    Message newestMessage = new Message().setThreadId("thread id").setCreatedAt(now());
    Message olderMessage = new Message().setThreadId("thread id").setCreatedAt(now().minus(1, HOURS));

    inTransaction(() -> repository.create(oldestMessage));
    inTransaction(() -> repository.create(newestMessage));
    inTransaction(() -> repository.create(olderMessage));

    Optional<Message> result = repository.lastMessage("thread id");

    assertThat(result).isNotEmpty();
    assertThat(result.get().getId()).isEqualTo(newestMessage.getId());
  }

  @Test
  public void lastThreadMessage_noMessagesYet() {
    assertThat(repository.lastMessage("thread id")).isEmpty();
  }
}