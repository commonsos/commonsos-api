package commonsos.domain.auth;

import commonsos.AuthenticationException;
import commonsos.DisplayableException;
import commonsos.domain.agreement.AccountCreateCommand;
import commonsos.domain.transaction.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @Mock UserRepository repository;
  @Mock TransactionService transactionService;
  @InjectMocks UserService service;

  @Test
  public void checkPassword_withValidUser() {
    User user = new User().setPasswordHash("secret");
    when(repository.find("worker")).thenReturn(Optional.of(user));

    assertThat(service.checkPassword("worker", "secret")).isEqualTo(user);
  }

  @Test(expected = AuthenticationException.class)
  public void checkPassword_withInvalidUsername() {
    when(repository.find("invalid")).thenReturn(Optional.empty());

    service.checkPassword("invalid", "secret");
  }

  @Test(expected = AuthenticationException.class)
  public void checkPassword_withInvalidPassword() {
    when(repository.find("user")).thenReturn(Optional.of(new User().setPasswordHash("secret")));

    service.checkPassword("user", "wrong password");
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

  @Test
  public void create() {
    User createdUser = new User();
    when(repository.create(any())).thenReturn(createdUser);

    User result = service.create(new AccountCreateCommand()
      .setUsername("user name")
      .setPassword("secret")
      .setFirstName("first")
      .setLastName("last")
    );

    assertThat(result).isEqualTo(createdUser);
    verify(repository).create(new User().setUsername("user name").setPasswordHash("secret").setFirstName("first").setLastName("last"));
  }

  @Test
  public void create_usernameAlreadyTaken() {
    when(repository.find("worker")).thenReturn(Optional.of(new User()));

    AccountCreateCommand command = new AccountCreateCommand()
      .setUsername("worker")
      .setPassword("secret")
      .setFirstName("first")
      .setLastName("last");
    DisplayableException thrown = catchThrowableOfType(()-> service.create(command), DisplayableException.class);

    assertThat(thrown).hasMessage("Username is already taken");
  }
}