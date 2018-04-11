package commonsos.domain.auth;

import commonsos.AuthenticationException;
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
    return new UserView().setId(user.getId()).setUsername(user.getUsername()).setBalance(balance);
  }
}
