package commonsos.domain.transaction;

import commonsos.DBTest;
import commonsos.domain.auth.User;
import org.junit.Test;

import java.math.BigDecimal;

import static commonsos.TestId.id;
import static java.math.BigDecimal.TEN;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionRepositoryTest extends DBTest {

  TransactionRepository repository = new TransactionRepository(entityManagerService);

  @Test
  public void create() {
    Long id = inTransaction(() -> repository.create(new Transaction()
        .setRemitterId(id("remitter id"))
        .setBeneficiaryId(id("beneficiary id"))
        .setAdId(id("ad id"))
        .setDescription("description")
        .setCreatedAt(parse("2017-10-24T11:22:33Z"))
        .setAmount(TEN))
      .getId());

    Transaction result = em().find(Transaction.class, id);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getRemitterId()).isEqualTo(id("remitter id"));
    assertThat(result.getBeneficiaryId()).isEqualTo(id("beneficiary id"));
    assertThat(result.getAdId()).isEqualTo(id("ad id"));
    assertThat(result.getDescription()).isEqualTo("description");
    assertThat(result.getCreatedAt()).isEqualTo(parse("2017-10-24T11:22:33Z"));
    assertThat(result.getAmount()).isEqualTo(new BigDecimal("10.00"));
  }

  @Test
  public void transactions() {
    User user = new User().setId(id("worker"));

    Long id1 = inTransaction(() -> repository.create(transaction(id("elderly"), id("worker"))).getId());
    Long id2 = inTransaction(() -> repository.create(transaction(id("worker"), id("elderly2"))).getId());
    inTransaction(() -> repository.create(transaction(id("foo"), id("bar"))).getId());

    assertThat(repository.transactions(user)).extracting("id").containsExactly(id1, id2);
  }

  private Transaction transaction(Long remitterId, Long beneficiary) {
    return new Transaction().setBeneficiaryId(beneficiary).setRemitterId(remitterId);
  }
}