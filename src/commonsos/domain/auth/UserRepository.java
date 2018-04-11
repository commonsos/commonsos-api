package commonsos.domain.auth;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserRepository {
  List<User> users = new ArrayList<>();

  {
    create(new User().setUsername("worker").setPasswordHash("secret").setFirstName("Haruto").setLastName("Sato"));
    create(new User().setUsername("elderly1").setPasswordHash("secret1").setFirstName("Riku").setLastName("Suzuki"));
    create(new User().setUsername("elderly2").setPasswordHash("secret2").setFirstName("Haru").setLastName("Takahashi"));
  }

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
