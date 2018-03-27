package commonsos.domain.auth;

import commonsos.AuthenticationException;
import commonsos.domain.transaction.TransactionService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class UserService {

  @Inject TransactionService transactionService;

  protected Map<String, String> users = new HashMap<String, String>() {{
    this.put("worker", "secret");
    this.put("elderly1", "secret1");
    this.put("elderly2", "secret2");
  }};

  public User login(String username, String password) {
    User user = userByUsername(username);
    if (!user.getPasswordHash().equals(password)) throw new AuthenticationException();

    return user;
  }

  public UserView view(User user) {
    BigDecimal balance = transactionService.balance(user);
    return new UserView().setUsername(user.getUsername()).setBalance(balance);
  }

  private User userByUsername(String username) {
    if (!users.containsKey(username)) throw new AuthenticationException();
    return new User().setId(username).setUsername(username).setPasswordHash(users.get(username));
  }
}
