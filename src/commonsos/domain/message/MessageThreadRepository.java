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

  public Optional<MessageThread> byAdId(User user, Long adId) {
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

  public Optional<MessageThread> betweenUsers(Long userId1, Long userId2) {
    String sql = "SELECT * FROM message_threads mt " +
      "WHERE mt.ad_id IS NULL AND " +
            "mt.id IN (SELECT mtp.message_thread_id FROM message_thread_parties mtp WHERE mtp.user_id = :user1 AND mt.id = mtp.message_thread_id) AND " +
            "mt.id IN (SELECT mtp.message_thread_id FROM message_thread_parties mtp WHERE mtp.user_id = :user2 AND mt.id = mtp.message_thread_id)";
    try {
      Object singleResult = em().createNativeQuery(sql, MessageThread.class)
        .setParameter("user1", userId1)
        .setParameter("user2", userId2)
        .getSingleResult();

      return Optional.of((MessageThread)singleResult);
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
      .createQuery("SELECT mt FROM MessageThread mt JOIN mt.parties p WHERE p.user = :user", MessageThread.class)
      .setParameter("user", user)
      .getResultList();
  }

  public Optional<MessageThread> thread(Long id) {
    return ofNullable(em().find(MessageThread.class, id));
  }

  public void update(MessageThreadParty party) {
    em().merge(party);
  }

  public List<Long> unreadMessageThreadIds(User user) {
    return em().createQuery(
      "SELECT mt.id " +
        "FROM MessageThread mt JOIN MessageThreadParty mtp ON mt.id = mtp.messageThreadId " +
        "WHERE mtp.user = :user " +
        "AND mt.id IN(SELECT threadId FROM Message WHERE threadId = mt.id) "+
        "AND (mtp.visitedAt IS NULL OR mtp.visitedAt < (SELECT MAX(m.createdAt) FROM Message m WHERE m.threadId = mt.id))", Long.class)
      .setParameter("user", user).getResultList();
  }
}
