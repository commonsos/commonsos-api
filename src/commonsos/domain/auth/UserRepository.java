package commonsos.domain.auth;

import commonsos.EntityManagerService;
import commonsos.Repository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static spark.utils.StringUtils.isBlank;

@Singleton
public class UserRepository extends Repository {

  @Inject
  public UserRepository(EntityManagerService entityManagerService) {
    super(entityManagerService);
  }

  public Optional<User> findByUsername(String username) {
    try {
      return Optional.of(em().createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
        .setParameter("username", username)
        .getSingleResult()
      );
    }
    catch (NoResultException e) {
      return empty();
    }
  }

  public User create(User user) {
    em().persist(user);
    return user;
  }

  public Optional<User> findById(String id) {
    return ofNullable(em().find(User.class, id));
  }

  public List<User> search(String query) {
    if (isBlank(query)) return emptyList();
    return em().createQuery("SELECT u FROM User u WHERE u.admin = FALSE AND u.firstName LIKE :query OR lastName LIKE :query", User.class)
      .setParameter("query", "%"+query+"%")
      .setMaxResults(10)
      .getResultList();
  }
}
