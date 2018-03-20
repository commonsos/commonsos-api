package commonsos.domain.agreement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AgreementRepositoryTest {
  @InjectMocks AgreementRepository repository;

  @Test
  public void create() {
    Agreement agreement = new Agreement();

    repository.create(agreement);

    assertThat(repository.agreements).contains(agreement);
    assertThat(agreement.getId()).isEqualTo("0");
  }

  @Test
  public void consumedBy() {
    Agreement agreement = new Agreement().setConsumerId("user");
    repository.agreements = asList(agreement, new Agreement().setConsumerId("other user"));

    assertThat(repository.consumedBy("user")).containsExactly(agreement);
  }

  @Test
  public void find() {
    Agreement agreement = new Agreement().setId("agreement id");
    repository.agreements = asList(agreement, new Agreement().setId("other agreement id"));

    assertThat(repository.find("agreement id")).contains(agreement);
  }
}