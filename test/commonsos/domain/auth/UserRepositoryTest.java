package commonsos.domain.auth;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest {

  UserRepository repository = new UserRepository();

  @Before
  public void setUp() throws Exception {
    repository.users.clear();
  }

  @Test
  public void find() {
    repository.users = asList(new User().setUsername("worker"));

    assertThat(repository.find("worker")).isNotEmpty();
  }

  @Test
  public void find_notFound() {
    assertThat(repository.find("worker")).isEmpty();
  }

  @Test
  public void create() {
    repository.create(new User().setUsername("worker"));

    Optional<User> result = repository.find("worker");
    assertThat(result).isNotEmpty();
    assertThat(result.get().getId()).isEqualTo("0");
  }
}