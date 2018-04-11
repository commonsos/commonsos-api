package commonsos.domain.auth;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

@Singleton
public class UserRepository {
  List<User> users = new ArrayList<>(asList(
      new User().setId("worker").setUsername("worker").setPasswordHash("secret"),
      new User().setId("elderly1").setUsername("elderly1").setPasswordHash("secret1"),
      new User().setId("elderly2").setUsername("elderly2").setPasswordHash("secret2"))
    );

  public Optional<User> find(String username) {
    return users.stream().filter(u -> u.getUsername().equals(username)).findAny();
  }
}
