package commonsos.domain.transaction;

import commonsos.DBTest;
import commonsos.domain.auth.User;
import org.junit.Test;

import java.math.BigDecimal;

import static java.math.BigDecimal.TEN;
import static java.time.OffsetDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionRepositoryTest extends DBTest {

  TransactionRepository repository = new TransactionRepository(entityManagerService);

  @Test
  public void create() {
    String id = inTransaction(() -> repository.create(new Transaction()
        .setRemitterId("remitter id")
        .setBeneficiaryId("beneficiary id")
        .setAdId("ad id")
        .setDescription("description")
        .setCreatedAt(parse("2017-10-24T11:22:33+03:00"))
        .setAmount(TEN))
      .getId());

    Transaction result = em().find(Transaction.class, id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getRemitterId()).isEqualTo("remitter id");
    assertThat(result.getBeneficiaryId()).isEqualTo("beneficiary id");
    assertThat(result.getAdId()).isEqualTo("ad id");
    assertThat(result.getDescription()).isEqualTo("description");
    assertThat(result.getCreatedAt()).isEqualTo(parse("2017-10-24T11:22:33+03:00"));
    assertThat(result.getAmount()).isEqualTo(new BigDecimal("10.00"));
  }

  @Test
  public void transactions() {
    User user = new User().setId("worker");

    String id1 = inTransaction(() -> repository.create(transaction("elderly", "worker")).getId());
    String id2 = inTransaction(() -> repository.create(transaction("worker", "elderly2")).getId());
    inTransaction(() -> repository.create(transaction("foo", "bar")).getId());

    assertThat(repository.transactions(user)).extracting("id").containsExactly(id1, id2);
  }

  private Transaction transaction(String remitterId, String beneficiary) {
    return new Transaction().setBeneficiaryId(beneficiary).setRemitterId(remitterId);
  }
}