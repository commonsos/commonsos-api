package commonsos.domain.agreement;

import commonsos.ForbiddenException;
import commonsos.domain.ad.Ad;
import commonsos.domain.auth.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
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
    service.create(
      new User().setId("elderly"),
      new Ad().setId("adId").setCreatedBy("worker").setLocation("home").setDescription("description").setTitle("title").setPoints(ONE));

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
      .setId("agreement id")
      .setTitle("title")
      .setDescription("description")
      .setLocation("home")
      .setPoints(BigDecimal.TEN)
      .setConsumerId("elderly")
      .setProviderId("worker");
    when(repository.find("agreement id")).thenReturn(Optional.of(agreement));

    AgreementViewModel result = service.details(new User().setId("elderly"), "agreement id");

    assertThat(result.getId()).isEqualTo("agreement id");
    assertThat(result.getTitle()).isEqualTo("title");
    assertThat(result.getDescription()).isEqualTo("description");
    assertThat(result.getLocation()).isEqualTo("home");
    assertThat(result.getAmount()).isEqualTo(TEN);
    assertThat(result.getTransactionData()).isEqualTo("c2FsdGFncmVlbWVudCBpZA==");
  }

  @Test(expected = RuntimeException.class)
  public void details_agreementNotFound() {
    when(repository.find("unknown agreement")).thenReturn(Optional.empty());

    service.details(new User().setId("user id"), "unknown agreement");
  }

  @Test(expected = ForbiddenException.class)
  public void details_isAvailableForAgreementParty() {
    when(repository.find("agreement id")).thenReturn(Optional.of(new Agreement().setConsumerId("someone")));

    service.details(new User().setId("user id"), "agreement id");
  }

  @Test
  public void findByTransactionData() {
    Agreement agreement = new Agreement();
    when(repository.find("agreement id")).thenReturn(Optional.of(agreement));

    Optional<Agreement> result = service.findByTransactionData("c2FsdGFncmVlbWVudCBpZA==");

    assertThat(result).contains(agreement);
  }

  @Test
  public void findByTransactionData_failsForUnknownData() {
    when(repository.find("agreement id")).thenReturn(Optional.empty());

    service.findByTransactionData("c2FsdGFncmVlbWVudCBpZA==");
  }

  @Test
  public void list() {
    Agreement agreement3 = new Agreement().setCreatedAt(OffsetDateTime.of(2010, 3, 30, 0, 0, 0, 0, ZoneOffset.UTC));
    Agreement agreement1 = new Agreement().setCreatedAt(OffsetDateTime.of(2010, 3, 10, 0, 0, 0, 0, ZoneOffset.UTC));
    Agreement agreement2 = new Agreement().setCreatedAt(OffsetDateTime.of(2010, 3, 20, 0, 0, 0, 0, ZoneOffset.UTC));
    when(repository.consumedBy("user id")).thenReturn(asList(agreement3, agreement1, agreement2));

    List<Agreement> result = service.list(new User().setId("user id"));

    assertThat(result).containsExactly(agreement3, agreement2, agreement1);
  }
}