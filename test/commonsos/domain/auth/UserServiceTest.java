package commonsos.domain.auth;

import commonsos.AuthenticationException;
import commonsos.BadRequestException;
import commonsos.DisplayableException;
import commonsos.domain.agreement.AccountCreateCommand;
import commonsos.domain.transaction.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @Mock UserRepository repository;
  @Mock TransactionService transactionService;
  @Mock PasswordService passwordService;
  @InjectMocks @Spy UserService service;

  @Test
  public void checkPassword_withValidUser() {
    User user = new User().setPasswordHash("hash");
    when(passwordService.passwordMatchesHash("secret", "hash")).thenReturn(true);
    when(repository.findByUsername("worker")).thenReturn(Optional.of(user));

    assertThat(service.checkPassword("worker", "secret")).isEqualTo(user);
  }

  @Test(expected = AuthenticationException.class)
  public void checkPassword_withInvalidUsername() {
    when(repository.findByUsername("invalid")).thenReturn(Optional.empty());

    service.checkPassword("invalid", "secret");
  }

  @Test(expected = AuthenticationException.class)
  public void checkPassword_withInvalidPassword() {
    when(repository.findByUsername("user")).thenReturn(Optional.of(new User().setPasswordHash("hash")));
    when(passwordService.passwordMatchesHash("wrong password", "hash")).thenReturn(false);

    service.checkPassword("user", "wrong password");

    verify(passwordService).passwordMatchesHash("wrong password", "hash");
  }

  @Test
  public void view() {
    User user = new User().setId("user id").setFirstName("first").setLastName("last");
    when(transactionService.balance(user)).thenReturn(BigDecimal.TEN);

    UserView view = service.view(user);

    assertThat(view.getId()).isEqualTo("user id");
    assertThat(view.getBalance()).isEqualTo(BigDecimal.TEN);
    assertThat(view.getFullName()).isEqualTo("last first");
  }

  @Test
  public void create() {
    User createdUser = new User();
    when(repository.create(any())).thenReturn(createdUser);
    when(passwordService.hash("secret")).thenReturn("hash");

    User result = service.create(new AccountCreateCommand()
      .setUsername("user name")
      .setPassword("secret")
      .setFirstName("first")
      .setLastName("last")
    );

    assertThat(result).isEqualTo(createdUser);
    verify(repository).create(new User().setUsername("user name").setPasswordHash("hash").setFirstName("first").setLastName("last"));
  }

  @Test
  public void create_usernameAlreadyTaken() {
    when(repository.findByUsername("worker")).thenReturn(Optional.of(new User()));

    AccountCreateCommand command = new AccountCreateCommand()
      .setUsername("worker")
      .setPassword("secret")
      .setFirstName("first")
      .setLastName("last");
    DisplayableException thrown = catchThrowableOfType(()-> service.create(command), DisplayableException.class);

    assertThat(thrown).hasMessage("Username is already taken");
  }

  @Test
  public void viewByUserId() {
    User user = new User();
    when(repository.findById("user id")).thenReturn(Optional.of(user));
    UserView view = new UserView();
    doReturn(view).when(service).view(user);

    assertThat(service.view("user id")).isEqualTo(view);
  }

  @Test(expected = BadRequestException.class)
  public void viewByUserId_userNotFound() {
    when(repository.findById("invalid id")).thenReturn(Optional.empty());

    service.view("invalid id");
  }
}