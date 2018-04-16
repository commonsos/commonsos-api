package commonsos.domain.auth;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static spark.utils.StringUtils.isBlank;

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

  public List<User> search(String query) {
    if (isBlank(query)) return emptyList();
    return users.stream().filter(matchUser(query)).limit(10).collect(toList());
  }

  private Predicate<User> matchUser(String query) {
    String normalizedQuery = query.toUpperCase();
    return u -> matches(u.getFirstName(), normalizedQuery) || matches(u.getLastName(), normalizedQuery);
  }

  private boolean matches(String testedValue, String query) {
    return testedValue.toUpperCase().contains(query);
  }
}
