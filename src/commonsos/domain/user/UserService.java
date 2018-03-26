package commonsos.domain.user;

import commonsos.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

public class UserService {
  private Map<String, String> users = new HashMap<String, String>() {{
    this.put("worker", "secret");
    this.put("elderly1", "secret1");
    this.put("elderly2", "secret2");
  }};

  public Session login(String username, String password) {
    if (!password.equals(users.get(username))) throw new AuthenticationException();

    return new Session().setToken(username).setUsername(username);
  }
}
