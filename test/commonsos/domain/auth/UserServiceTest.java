package commonsos.domain.auth;

import commonsos.AuthenticationException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest {

  UserService service = new UserService();

  @Test
  public void login_withValidUser() {
    User user = service.login("worker", "secret");

    assertThat(user.getId()).isEqualTo("worker");
    assertThat(user.getUsername()).isEqualTo("worker");
    assertThat(user.getPasswordHash()).isEqualTo("secret");
  }

  @Test(expected = AuthenticationException.class)
  public void login_withInvalidPassword() {
    service.login("worker", "wrong password");
  }

  @Test(expected = AuthenticationException.class)
  public void login_withInvalidUser() {
    service.login("wrong user", "password");
  }

  @Test
  public void view() {
    User user = new User().setUsername("username");
    assertThat(service.view(user).getUsername()).isEqualTo("username");
  }
}