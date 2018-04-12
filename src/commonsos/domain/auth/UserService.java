package commonsos.domain.auth;

import commonsos.AuthenticationException;
import commonsos.BadRequestException;
import commonsos.DisplayableException;
import commonsos.domain.agreement.AccountCreateCommand;
import commonsos.domain.transaction.TransactionService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;

@Singleton
public class UserService {
  @Inject UserRepository repository;
  @Inject TransactionService transactionService;
  @Inject PasswordService passwordService;

  public User checkPassword(String username, String password) {
    User user = repository.findByUsername(username).orElseThrow(AuthenticationException::new);
    if (!passwordService.passwordMatchesHash(password, user.getPasswordHash())) throw new AuthenticationException();
    return user;
  }

  public UserView view(User user) {
    BigDecimal balance = transactionService.balance(user);
    return new UserView()
      .setId(user.getId())
      .setBalance(balance)
      .setFullName(String.format("%s %s", user.getLastName(), user.getFirstName()));
  }

  public User create(AccountCreateCommand command) {
    if (repository.findByUsername(command.getUsername()).isPresent()) throw new DisplayableException("Username is already taken");

    User user = new User()
      .setUsername(command.getUsername())
      .setPasswordHash(passwordService.hash(command.getPassword()))
      .setFirstName(command.getFirstName())
      .setLastName(command.getLastName());

    return repository.create(user);
  }

  public UserView view(String id) {
    return view(user(id));
  }

  private User user(String id) {
    return repository.findById(id).orElseThrow(BadRequestException::new);
  }
}
