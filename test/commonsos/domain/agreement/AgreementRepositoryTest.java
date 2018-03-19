package commonsos.domain.agreement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AgreementRepositoryTest {
  @InjectMocks AgreementRepository repository;

  @Test
  public void create() {
    Agreement agreement = new Agreement();

    repository.create(agreement);

    assertThat(repository.agreements).contains(agreement);
  }
}