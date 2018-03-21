package commonsos.domain.reward;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
  public List<Transaction> transactions = new ArrayList<>();

  public void create(Transaction transaction) {
    transactions.add(transaction);
  }
}
