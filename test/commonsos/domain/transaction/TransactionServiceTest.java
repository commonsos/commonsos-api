package commonsos.domain.transaction;

import commonsos.DisplayableException;
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
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

  @Mock AgreementService agreementService;
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
    TransactionCreateCommand command = new TransactionCreateCommand()
      .setBeneficiaryId("beneficiary")
      .setAmount(new BigDecimal("10.2"))
      .setDescription("description");
    User user = new User().setId("remitter");
    doReturn(new BigDecimal("10.20")).when(service).balance(user);

    service.create(user, command);

    verify(repository).create(captor.capture());
    Transaction transaction = captor.getValue();
    assertThat(transaction.getAmount()).isEqualTo(new BigDecimal("10.2"));
    assertThat(transaction.getBeneficiaryId()).isEqualTo("beneficiary");
    assertThat(transaction.getRemitterId()).isEqualTo("remitter");
    assertThat(transaction.getDescription()).isEqualTo("description");
    assertThat(transaction.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
  }

  @Test
  public void create_insufficientBalance() {
    TransactionCreateCommand command = new TransactionCreateCommand()
      .setBeneficiaryId("beneficiary")
      .setAmount(new BigDecimal("10.2"))
      .setDescription("description");
    User user = new User().setId("remitter");
    doReturn(TEN).when(service).balance(user);

    DisplayableException thrown = catchThrowableOfType(() -> service.create(user, command), DisplayableException.class);

    assertThat(thrown).hasMessage("Not enough funds");
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
        .setCreatedAt(OffsetDateTime.MAX));

    assertThat(view.getBeneficiary()).isEqualTo(beneficiary);
    assertThat(view.getRemitter()).isEqualTo(remitter);
    assertThat(view.getAmount()).isEqualTo(TEN);
    assertThat(view.getCreatedAt()).isEqualTo(OffsetDateTime.MAX);
    assertThat(view.isDebit()).isTrue();
  }

  @Test
  public void transactions() {
    User user = new User();
    Transaction transaction = new Transaction();
    when(repository.transactions(user)).thenReturn(asList(transaction));
    TransactionView transactionView = new TransactionView();
    doReturn(transactionView).when(service).view(user, transaction);

    List<TransactionView> result = service.transactions(user);

    assertThat(result).isEqualTo(asList(transactionView));
  }
}