package commonsos.domain.agreement;

import commonsos.domain.ad.Ad;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.math.BigDecimal.ONE;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AgreementServiceTest {

  @Mock AgreementRepository repository;
  @InjectMocks AgreementService service = new AgreementService();

  @Test
  public void create() {
    service.create("elderly", new Ad().setId("adId").setCreatedBy("worker").setLocation("home").setDescription("description").setTitle("title").setPoints(ONE));

    ArgumentCaptor<Agreement> captor = forClass(Agreement.class);
    verify(repository).create(captor.capture());
    Agreement agreement = captor.getValue();
    assertThat(agreement).isNotNull();
    assertThat(agreement.getAdId()).isEqualTo("adId");
    assertThat(agreement.getConsumerId()).isEqualTo("elderly");
    assertThat(agreement.getProviderId()).isEqualTo("worker");
    assertThat(agreement.getTitle()).isEqualTo("title");
    assertThat(agreement.getDescription()).isEqualTo("description");
    assertThat(agreement.getLocation()).isEqualTo("home");
    assertThat(agreement.getPoints()).isEqualTo(ONE);
    assertThat(agreement.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
  }
}