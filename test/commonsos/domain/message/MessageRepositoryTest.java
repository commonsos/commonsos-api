package commonsos.domain.message;

import commonsos.domain.auth.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MessageRepositoryTest {

  @InjectMocks MessageRepository repository;

  @Test
  public void byAdId() {
    MessageThread thread1 = new MessageThread().setAdId("10").setCreatedBy("me");
    MessageThread thread2 = new MessageThread().setAdId("20").setCreatedBy("other-user");
    MessageThread thread3 = new MessageThread().setAdId("20").setCreatedBy("me");
    repository.threads.addAll(asList(thread1, thread2, thread3));

    assertThat(repository.byAdId(new User().setId("me"), "20")).contains(thread3);
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

  @Test
  public void listByUser() {
    User user = new User().setId("1");
    MessageThread thread1 = new MessageThread().setUsers(asList(user, new User().setId("2")));
    MessageThread thread2 = new MessageThread().setUsers(asList(new User().setId("2"), new User().setId("3")));
    MessageThread thread3 = new MessageThread().setUsers(asList(new User().setId("3"), user));
    repository.threads.addAll(asList(thread1, thread2, thread3));

    List<MessageThread> result = repository.listByUser(user);

    assertThat(result).containsExactly(thread1, thread3);
  }

  @Test
  public void threadById() {
    MessageThread thread1 = new MessageThread().setId("0");
    MessageThread thread2 = new MessageThread().setId("1");
    repository.threads.addAll(asList(thread1, thread2));

    assertThat(repository.thread("1")).contains(thread2);
  }

  @Test
  public void threadById_notFound() {
    repository.threads.addAll(asList(new MessageThread().setId("0")));

    assertThat(repository.thread("1")).isEmpty();
  }
}