package commonsos.domain.auth;

import commonsos.AuthenticationException;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class UserService {
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
    return new UserView().setUsername(user.getUsername());
  }

  private User userByUsername(String username) {
    if (!users.containsKey(username)) throw new AuthenticationException();
    return new User().setId(username).setUsername(username).setPasswordHash(users.get(username));
  }
}
