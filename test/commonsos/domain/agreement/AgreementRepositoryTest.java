package commonsos.domain.agreement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

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

  @Test
  public void update() {
    repository.agreements = new ArrayList<>(asList(new Agreement().setId("agreement id")));

    repository.update(new Agreement().setId("agreement id").setRewardClaimedAt(now()));

    assertThat(repository.agreements.get(0).getRewardClaimedAt()).isCloseTo(now(), within(1, SECONDS));
  }
}