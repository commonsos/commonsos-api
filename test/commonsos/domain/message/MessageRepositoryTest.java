package commonsos.domain.message;

import org.junit.Test;

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
}