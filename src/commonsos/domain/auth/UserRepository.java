package commonsos.domain.auth;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserRepository {
  List<User> users = new ArrayList<>();

  public Optional<User> findByUsername(String username) {
    return users.stream().filter(u -> u.getUsername().equals(username)).findAny();
  }

  public User create(User user) {
    user.setId(String.valueOf(users.size()));
    users.add(user);
    return user;
  }

  public Optional<User> findById(String id) {
    return users.stream().filter(u -> u.getId().equals(id)).findAny();
  }
}
