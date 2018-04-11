package commonsos.domain.transaction;

import commonsos.domain.auth.User;

import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.stream.Collectors.toList;

public class TransactionRepository {
  public List<Transaction> transactions = new ArrayList<Transaction>() {{
    add(new Transaction().setBeneficiaryId("0").setRemitterId("1").setAmount(TEN).setCreatedAt(now().minus(1, HOURS)));
    add(new Transaction().setBeneficiaryId("1").setRemitterId("0").setAmount(ONE).setCreatedAt(now()));
    add(new Transaction().setBeneficiaryId("2").setRemitterId("1").setAmount(ONE.add(ONE)).setCreatedAt(now().minus(3, HOURS)));
    add(new Transaction().setBeneficiaryId("0").setRemitterId("2").setAmount(TEN.add(TEN)).setCreatedAt(now().minus(51, HOURS)));
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
