package commonsos.domain.transaction;

import commonsos.EntityManagerService;
import commonsos.Repository;
import commonsos.domain.auth.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Singleton
public class TransactionRepository extends Repository {

  @Inject
  public TransactionRepository(EntityManagerService entityManagerService) {
    super(entityManagerService);
  }

  public Transaction create(Transaction transaction) {
    em().persist(transaction);
    return transaction;
  }

  public List<Transaction> transactions(User user) {
    return em()
      .createQuery("FROM Transaction WHERE beneficiaryId = :userId OR remitterId = :userId", Transaction.class)
      .setParameter("userId", user.getId())
      .getResultList();
  }

  public void update(Transaction transaction) {
    em().merge(transaction);
  }

  public Optional<Transaction> findByBlockchainTransactionHash(String blockchainTransactionHash) {
    try {
      return of(em()
        .createQuery("From Transaction WHERE blockchainTransactionHash = :hash", Transaction.class)
        .setParameter("hash", blockchainTransactionHash)
        .getSingleResult());
    }
    catch (NoResultException e) {
        return empty();
    }
  }
}
