package commonsos.domain.reward;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

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
}