package commonsos.domain.message;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class MessageRepositoryTest {

  private MessageRepository repository = new MessageRepository();

  @Test
  public void createMessage() {
    repository.messages.clear();
    Message message = new Message();

    Message result = repository.create(message);

    assertThat(result.getId()).isEqualTo("0");
  }

  @Test
  public void listByThread() {
    Message message1 = new Message().setThreadId("thread id");
    Message message2 = new Message().setThreadId("thread id");
    repository.messages.addAll(asList(new Message().setThreadId("other thread"), message1, message2));

    List<Message> result = repository.listByThread("thread id");

    assertThat(result).containsExactly(message1, message2);
  }

}