package commonsos.domain.reward;

import commonsos.domain.auth.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TransactionRepositoryTest {

  @InjectMocks TransactionRepository repository;

  @Test
  public void create() {
    Transaction transaction = new Transaction();

    repository.create(transaction);

    assertThat(repository.transactions).contains(transaction);
  }

  @Test
  public void transactions() {
    User user = new User().setId("worker");
    Transaction incoming = transaction("elderly", "worker");
    Transaction outgoing = transaction("worker", "elderly2");
    repository.transactions = asList(incoming, transaction("john", "doe"), outgoing, transaction("foo", "bar"));

    assertThat(repository.transactions(user)).containsExactly(incoming, outgoing);
  }

  private Transaction transaction(String remitterId, String beneficiary) {
    return new Transaction().setBeneficiaryId(beneficiary).setRemitterId(remitterId);
  }
}