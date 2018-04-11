package commonsos.domain.auth;

import commonsos.AuthenticationException;
import commonsos.domain.transaction.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @Mock UserRepository repository;
  @Mock TransactionService transactionService;
  @InjectMocks UserService service;

  @Test
  public void login_withValidUser() {
    User user = new User().setPasswordHash("secret");
    when(repository.find("worker")).thenReturn(Optional.of(user));

    assertThat(service.login("worker", "secret")).isEqualTo(user);
  }

  @Test(expected = AuthenticationException.class)
  public void login_withInvalidUsername() {
    when(repository.find("invalid")).thenReturn(Optional.empty());

    service.login("invalid", "secret");
  }

  @Test(expected = AuthenticationException.class)
  public void login_withInvalidPassword() {
    when(repository.find("user")).thenReturn(Optional.of(new User().setPasswordHash("secret")));

    service.login("user", "wrong password");
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