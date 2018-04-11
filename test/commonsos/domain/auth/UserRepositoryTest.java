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
  public void findByUsername() {
    repository.users = asList(new User().setUsername("worker"));

    assertThat(repository.findByUsername("worker")).isNotEmpty();
  }

  @Test
  public void findByUsername_notFound() {
    assertThat(repository.findByUsername("worker")).isEmpty();
  }

  @Test
  public void findById() {
    repository.users = asList(new User().setId("user id"));

    assertThat(repository.findById("user id")).isNotEmpty();
  }

  @Test
  public void findById_notFound() {
    assertThat(repository.findById("invalid id")).isEmpty();
  }

  @Test
  public void create() {
    repository.create(new User().setUsername("worker"));

    Optional<User> result = repository.findByUsername("worker");
    assertThat(result).isNotEmpty();
    assertThat(result.get().getId()).isEqualTo("0");
  }
}