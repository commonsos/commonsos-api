package commonsos.domain.transaction;

import commonsos.DBTest;
import commonsos.domain.auth.User;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static commonsos.TestId.id;
import static java.math.BigDecimal.*;
import static java.time.Instant.now;
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
      .setBlockchainTransactionHash("blockchain id")
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
    assertThat(result.getBlockchainTransactionHash()).isEqualTo("blockchain id");
  }

  @Test
  public void update() {
    Long id = inTransaction(() -> repository.create(new Transaction()
      .setAmount(TEN))
      .getId());

    inTransaction(() -> {
      Transaction existing = em().find(Transaction.class, id);
      repository.update(existing.setAmount(ONE));
    });

    Transaction result = em().find(Transaction.class, id);
    assertThat(result.getAmount()).isEqualByComparingTo(ONE);
  }

  @Test
  public void transactions() {
    User user = new User().setId(id("worker"));

    Long id1 = inTransaction(() -> repository.create(transaction(id("elderly"), id("worker"))).getId());
    Long id2 = inTransaction(() -> repository.create(transaction(id("worker"), id("elderly2"))).getId());
    inTransaction(() -> repository.create(transaction(id("foo"), id("bar"))).getId());

    assertThat(repository.transactions(user)).extracting("id").containsExactly(id1, id2);
  }

  @Test
  public void findByBlockchainTransactionHash() {
    inTransaction(() -> repository.create(new Transaction().setBlockchainTransactionHash("other value")));
    Long id = inTransaction(() -> repository.create(new Transaction().setBlockchainTransactionHash("hash value")).getId());

    Optional<Transaction> result = repository.findByBlockchainTransactionHash("hash value");

    assertThat(result).isNotEmpty();
    assertThat(result.get().getId()).isEqualTo(id);
  }

  @Test
  public void findByHash_notFound() {
    assertThat(repository.findByBlockchainTransactionHash("hash value")).isEmpty();
  }

  private Transaction transaction(Long remitterId, Long beneficiary) {
    return new Transaction().setBeneficiaryId(beneficiary).setRemitterId(remitterId);
  }

  @Test
  public void pendingTransactionsAmount() {
    inTransaction(() -> repository.create(new Transaction().setRemitterId(id("user")).setAmount(TEN)));
    inTransaction(() -> repository.create(new Transaction().setRemitterId(id("other user")).setAmount(TEN)));
    inTransaction(() -> repository.create(new Transaction().setRemitterId(id("user")).setAmount(ONE).setBlockchainCompletedAt(now())));

    BigDecimal amount = repository.pendingTransactionsAmount(id("user"));

    assertThat(amount).isEqualByComparingTo(TEN);
  }

  @Test
  public void pendingTransactionsAmount_none() {
    BigDecimal amount = repository.pendingTransactionsAmount(id("user"));

    assertThat(amount).isEqualByComparingTo(ZERO);
  }
}