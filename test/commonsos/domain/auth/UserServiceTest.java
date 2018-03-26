package commonsos.domain.auth;

import commonsos.AuthenticationException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServiceTest {

  UserService service = new UserService();

  @Test
  public void login_withValidUser() {
    Session session = service.login("worker", "secret");

    assertThat(session.getToken()).isEqualTo("worker");
    assertThat(session.getUsername()).isEqualTo("worker");
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
  public void userByToken() {
    service.users.put("token", null);

    assertThat(service.userByToken("token").getId()).isEqualTo("token");
  }

  @Test(expected = AuthenticationException.class)
  public void userByToken_notFound() {
    assertThat(service.userByToken("non valid token").getId()).isEqualTo("worker");
  }
}