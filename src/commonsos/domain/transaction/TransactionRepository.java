package commonsos.domain.transaction;

import commonsos.domain.auth.User;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class TransactionRepository {
  public List<Transaction> transactions = new ArrayList<>();

  public void create(Transaction transaction) {
    transactions.add(transaction);
  }

  public List<Transaction> transactions(User user) {
    return transactions
      .stream()
      .filter(t -> isUserRelated(user, t))
      .collect(toList());
  }

  private boolean isUserRelated(User user, Transaction t) {
    return t.getBeneficiaryId().equals(user.getId()) || t.getRemitterId().equals(user.getId());
  }
}
