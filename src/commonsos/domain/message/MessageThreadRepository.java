package commonsos.domain.message;

import commonsos.EntityManagerService;
import commonsos.Repository;
import commonsos.domain.auth.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Singleton
public class MessageThreadRepository extends Repository {

  @Inject
  public MessageThreadRepository(EntityManagerService entityManagerService) {
    super(entityManagerService);
  }

  public Optional<MessageThread> byAdId(User user, String adId) {
    try {
      return Optional.of(em().createQuery("FROM MessageThread WHERE adId = :adId AND createdBy = :createdBy", MessageThread.class)
        .setParameter("adId", adId)
        .setParameter("createdBy", user.getId())
        .getSingleResult());
    }
    catch (NoResultException e) {
      return empty();
    }
  }

  public MessageThread create(MessageThread messageThread) {
    em().persist(messageThread);
    return messageThread;
  }

  public List<MessageThread> listByUser(User user) {
    return em()
      .createQuery("SELECT mt FROM MessageThread mt JOIN mt.parties ps WHERE ps.id = :userId", MessageThread.class)
      .setParameter("userId", user.getId())
      .getResultList();
  }

  public Optional<MessageThread> thread(String id) {
    return ofNullable(em().find(MessageThread.class, id));
  }
}
