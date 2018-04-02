package commonsos.domain.transaction;

import commonsos.domain.auth.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.stream.Collectors.toList;

public class TransactionRepository {
  public List<Transaction> transactions = new ArrayList<Transaction>() {{
    add(new Transaction().setBeneficiaryId("worker").setRemitterId("elderly1").setAmount(BigDecimal.TEN).setCreatedAt(now().minus(1, HOURS)));
    add(new Transaction().setBeneficiaryId("elderly1").setRemitterId("worker").setAmount(BigDecimal.ONE).setCreatedAt(now()));
  }};

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
