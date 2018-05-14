package commonsos.domain.transaction;

import commonsos.EntityManagerService;
import commonsos.Repository;
import commonsos.domain.auth.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

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
}
