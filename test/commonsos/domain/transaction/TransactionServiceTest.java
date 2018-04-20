package commonsos.domain.transaction;

import commonsos.BadRequestException;
import commonsos.DisplayableException;
import commonsos.domain.ad.AdService;
import commonsos.domain.ad.AdView;
import commonsos.domain.agreement.Agreement;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

  @Mock AgreementService agreementService;
  @Mock AdService adService;
  @Mock UserService userService;
  @Captor ArgumentCaptor<Transaction> captor;
  @Mock TransactionRepository repository;
  @InjectMocks @Spy TransactionService service;

  @Test
  public void claim() {
    Agreement agreement = new Agreement().setPoints(TEN).setId("123").setProviderId("worker").setConsumerId("elderly");
    when(agreementService.findByTransactionData("transactionData")).thenReturn(Optional.of(agreement));

    Transaction result = service.claim(new User().setId("worker"), "transactionData");

    verify(agreementService).rewardClaimed(agreement);
    verify(repository).create(captor.capture());
    Transaction transaction = captor.getValue();
    assertThat(transaction.getAgreementId()).isEqualTo("123");
    assertThat(transaction.getAmount()).isEqualTo(TEN);
    assertThat(transaction.getBeneficiaryId()).isEqualTo("worker");
    assertThat(transaction.getRemitterId()).isEqualTo("elderly");
    assertThat(transaction.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
    assertThat(transaction).isEqualTo(result);
  }

  @Test
  public void create() {
    TransactionCreateCommand command = command("beneficiary", "10.2", "description", "ad id");
    User user = new User().setId("remitter");
    doReturn(new BigDecimal("10.20")).when(service).balance(user);
    when(adService.ad(user, "ad id")).thenReturn(new AdView().setCreatedBy(new UserView().setId("beneficiary")));

    service.create(user, command);

    verify(adService).ad(user, "ad id");
    verify(repository).create(captor.capture());
    Transaction transaction = captor.getValue();
    assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("10.2"));
    assertThat(transaction.getBeneficiaryId()).isEqualTo("beneficiary");
    assertThat(transaction.getRemitterId()).isEqualTo("remitter");
    assertThat(transaction.getDescription()).isEqualTo("description");
    assertThat(transaction.getAdId()).isEqualTo("ad id");
    assertThat(transaction.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
  }

  @Test
  public void create_transactionWithoutAd() {
    TransactionCreateCommand command = command("beneficiary", "10.2", "description", null);
    User user = new User().setId("remitter");
    doReturn(new BigDecimal("10.20")).when(service).balance(user);

    service.create(user, command);

    verify(repository).create(any());
  }

  private TransactionCreateCommand command(String beneficiary, String amount, String description, String adId) {
    return new TransactionCreateCommand()
      .setBeneficiaryId(beneficiary)
      .setAmount(new BigDecimal(amount))
      .setDescription(description)
      .setAdId(adId);
  }

  @Test
  public void create_insufficientBalance() {
    TransactionCreateCommand command = command("beneficiary", "10.2", "description", "ad id");
    User user = new User().setId("remitter");
    doReturn(TEN).when(service).balance(user);
    when(adService.ad(user, "ad id")).thenReturn(new AdView().setCreatedBy(new UserView().setId("beneficiary")));

    DisplayableException thrown = catchThrowableOfType(() -> service.create(user, command), DisplayableException.class);

    assertThat(thrown).hasMessage("Not enough funds");
  }

  @Test(expected = BadRequestException.class)
  public void create_unknownBeneficiary() {
    when(userService.user("unknown")).thenThrow(new BadRequestException());
    TransactionCreateCommand command = command("unknown", "10.2", "description", "33");

    service.create(new User(), command);
  }

  @Test(expected = BadRequestException.class)
  public void create_unknownAd() {
    TransactionCreateCommand command = command("beneficiary", "10.2", "description", "unknown ad");
    User user = new User().setId("remitter");
    when(adService.ad(user, "unknown ad")).thenThrow(new BadRequestException());

    service.create(user, command);
  }

  @Test(expected = BadRequestException.class)
  public void create_beneficiaryDontMatchWithAdOwner() {
    TransactionCreateCommand command = command("not ad owner", "10.2", "description", "unknown ad");
    User user = new User().setId("remitter");
    when(adService.ad(user, "unknown ad")).thenReturn(new AdView().setCreatedBy(new UserView().setId("ad owner")));

    service.create(user, command);
  }

  @Test
  public void claim_onlyProviderCanClaimReward() {
    when(agreementService.findByTransactionData("otherUserTransactionData")).thenReturn(Optional.of(new Agreement().setProviderId("worker")));

    DisplayableException thrown = catchThrowableOfType(() -> service.claim(new User().setId("other user id"), "otherUserTransactionData"), DisplayableException.class);

    assertThat(thrown).hasMessage("Only service provider can claim this code");
  }

  @Test
  public void claim_onlyOnceAllowed() {
    Agreement agreement = new Agreement().setPoints(TEN).setId("123").setProviderId("worker").setConsumerId("elderly").setRewardClaimedAt(now());
    when(agreementService.findByTransactionData("transactionData")).thenReturn(Optional.of(agreement));

    DisplayableException thrown = catchThrowableOfType(() -> service.claim(new User().setId("worker"), "transactionData"), DisplayableException.class);

    assertThat(thrown).hasMessage("This code has been already claimed");
  }

  @Test
  public void balance() {
    User user = new User().setId("worker");
    List<Transaction> transactions = asList(
      new Transaction().setRemitterId("worker").setBeneficiaryId("elderly").setAmount(ONE),
      new Transaction().setRemitterId("elderly").setBeneficiaryId("worker").setAmount(TEN));
    when(repository.transactions(user)).thenReturn(transactions);

    BigDecimal balance = service.balance(user);

    assertThat(balance).isEqualTo(new BigDecimal("9"));
  }

  @Test
  public void view() {
    UserView beneficiary = new UserView();
    UserView remitter = new UserView();
    when(userService.view("beneficiary id")).thenReturn(beneficiary);
    when(userService.view("remitter id")).thenReturn(remitter);

    TransactionView view = service.view(
      new User().setId("remitter id"),
      new Transaction()
        .setBeneficiaryId("beneficiary id")
        .setRemitterId("remitter id")
        .setAmount(TEN)
        .setDescription("description")
        .setCreatedAt(OffsetDateTime.MAX));

    assertThat(view.getBeneficiary()).isEqualTo(beneficiary);
    assertThat(view.getRemitter()).isEqualTo(remitter);
    assertThat(view.getAmount()).isEqualTo(TEN);
    assertThat(view.getDescription()).isEqualTo("description");
    assertThat(view.getCreatedAt()).isEqualTo(OffsetDateTime.MAX);
    assertThat(view.isDebit()).isTrue();
  }

  @Test
  public void transactions() {
    User user = new User();
    Transaction transaction1 = new Transaction().setCreatedAt(now().minus(1, HOURS));
    Transaction transaction2 = new Transaction().setCreatedAt(now());
    when(repository.transactions(user)).thenReturn(asList(transaction1, transaction2));
    TransactionView transactionView1 = new TransactionView();
    TransactionView transactionView2 = new TransactionView();
    doReturn(transactionView1).when(service).view(user, transaction1);
    doReturn(transactionView2).when(service).view(user, transaction2);

    List<TransactionView> result = service.transactions(user);

    assertThat(result).isEqualTo(asList(transactionView2, transactionView1));
  }
}