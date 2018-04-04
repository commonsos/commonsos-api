package commonsos.domain.auth;

import commonsos.AuthenticationException;
import commonsos.domain.transaction.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @Mock TransactionService transactionService;
  @InjectMocks UserService service;

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
    User user = new User().setUsername("username").setId("user id");
    when(transactionService.balance(user)).thenReturn(BigDecimal.TEN);

    UserView view = service.view(user);

    assertThat(view.getId()).isEqualTo("user id");
    assertThat(view.getUsername()).isEqualTo("username");
    assertThat(view.getBalance()).isEqualTo(BigDecimal.TEN);
  }
}