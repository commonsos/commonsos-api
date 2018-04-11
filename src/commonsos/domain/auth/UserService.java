package commonsos.domain.auth;

import commonsos.AuthenticationException;
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

  public User checkPassword(String username, String password) {
    User user = repository.find(username).orElseThrow(AuthenticationException::new);
    if (!user.getPasswordHash().equals(password)) throw new AuthenticationException();
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
    User user = new User()
      .setUsername(command.getUsername())
      .setPasswordHash(command.getPassword())
      .setFirstName(command.getFirstName())
      .setLastName(command.getLastName());

    if (repository.find(command.getUsername()).isPresent()) throw new DisplayableException("Username is already taken");

    return repository.create(user);
  }
}
