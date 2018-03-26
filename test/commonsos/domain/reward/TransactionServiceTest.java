package commonsos.domain.reward;

import commonsos.ForbiddenException;
import commonsos.domain.agreement.Agreement;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.math.BigDecimal.TEN;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

  @Mock TransactionRepository repository;
  @Mock AgreementService agreementService;
  @InjectMocks TransactionService transactionService;
  @Captor ArgumentCaptor<Transaction> captor;

  @Test
  public void claim() {
    Agreement agreement = new Agreement().setPoints(TEN).setId("123").setProviderId("worker").setConsumerId("elderly");
    when(agreementService.findByTransactionData("transactionData")).thenReturn(agreement);

    Transaction result = transactionService.claim(new User().setId("worker"), "transactionData");

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

  @Test(expected = ForbiddenException.class)
  public void claim_onlyProviderCanClaimReward() {
    when(agreementService.findByTransactionData("otherUserTransactionData")).thenReturn(new Agreement().setProviderId("worker"));

    transactionService.claim(new User().setId("other user id"), "otherUserTransactionData");
  }

  @Test(expected = ForbiddenException.class)
  public void claim_onlyOnceAllowed() {
    Agreement agreement = new Agreement().setPoints(TEN).setId("123").setProviderId("worker").setConsumerId("elderly").setRewardClaimedAt(now());
    when(agreementService.findByTransactionData("transactionData")).thenReturn(agreement);

    transactionService.claim(new User().setId("worker"), "transactionData");
  }
}