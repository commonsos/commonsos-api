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

  public Session login(String username, String password) {
    if (!password.equals(users.get(username))) throw new AuthenticationException();

    return new Session().setToken(username).setUsername(username);
  }

  public User userByToken(String token) {
   if (!users.containsKey(token)) throw new AuthenticationException();

   return new User().setId(token);
  }
}
