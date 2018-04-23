package commonsos.domain.message;

import commonsos.domain.auth.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MessageRepositoryTest {

  @InjectMocks MessageRepository repository;

  @Test
  public void byAdId() {
    MessageThread messageThread1 = new MessageThread().setAdId("10").setCreatedBy("me");
    MessageThread messageThread2 = new MessageThread().setAdId("20").setCreatedBy("other-user");
    MessageThread messageThread3 = new MessageThread().setAdId("20").setCreatedBy("me");
    repository.threads.addAll(asList(messageThread1, messageThread2, messageThread3));

    assertThat(repository.byAdId(new User().setId("me"), "20")).contains(messageThread3);
  }

  @Test
  public void byAdId_notFound() {
    repository.threads.clear();

    assertThat(repository.byAdId(new User().setId("me"), "20")).isEmpty();
  }

  @Test
  public void create() {
    repository.threads.clear();
    MessageThread messageThread = new MessageThread();

    MessageThread result = repository.create(messageThread);

    assertThat(result.getId()).isEqualTo("0");
    assertThat(repository.threads).containsExactly(messageThread);
  }
}