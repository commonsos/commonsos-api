package commonsos.domain.agreement;

import commonsos.ForbiddenException;
import commonsos.domain.ad.Ad;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

  @Test
  public void details() {
    Agreement agreement = new Agreement()
      .setTitle("title")
      .setDescription("description")
      .setLocation("home")
      .setPoints(BigDecimal.TEN)
      .setConsumerId("elderly")
      .setProviderId("worker");
    when(repository.find("agreement id")).thenReturn(Optional.of(agreement));

    AgreementViewModel result = service.details("elderly", "agreement id");

    assertThat(result.getId()).isEqualTo("agreement id");
    assertThat(result.getTitle()).isEqualTo("title");
    assertThat(result.getDescription()).isEqualTo("description");
    assertThat(result.getLocation()).isEqualTo("home");
    assertThat(result.getAmount()).isEqualTo(TEN);
    assertThat(result.getTransactionData()).isEqualTo("amount:10|from:elderly|to:worker");
  }

  @Test(expected = RuntimeException.class)
  public void details_agreementNotFound() {
    when(repository.find("unknown agreement")).thenReturn(Optional.empty());

    service.details("user id", "unknown agreement");
  }

  @Test(expected = ForbiddenException.class)
  public void details_isAvailableForAgreementParty() {
    when(repository.find("agreement id")).thenReturn(Optional.of(new Agreement().setConsumerId("someone")));

    service.details("user id", "agreement id");
  }
}