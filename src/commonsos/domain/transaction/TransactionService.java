package commonsos.domain.transaction;

import commonsos.DisplayableException;
import commonsos.domain.agreement.Agreement;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.auth.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static java.math.BigDecimal.ZERO;

@Singleton
public class TransactionService {
  @Inject AgreementService agreementService;
  @Inject TransactionRepository repository;

  public Transaction claim(User user, String transactionData) {
    Agreement agreement = agreementService.findByTransactionData(transactionData).orElseThrow(() -> new DisplayableException("Code not found"));

    if (!user.getId().equals(agreement.getProviderId())) throw new DisplayableException("Only service provider can claim this code");
    if (agreement.getRewardClaimedAt() != null) throw new DisplayableException("This code has been already claimed");

    Transaction transaction = new Transaction()
      .setAmount(agreement.getPoints())
      .setBeneficiaryId(agreement.getProviderId())
      .setRemitterId(agreement.getConsumerId())
      .setAgreementId(agreement.getId())
      .setCreatedAt(OffsetDateTime.now());

    repository.create(transaction);
    agreementService.rewardClaimed(agreement);

    return transaction;
  }

  public BigDecimal balance(User user) {
    return repository.transactions(user).stream()
      .map(transaction -> isDebit(user, transaction) ? transaction.getAmount().negate() : transaction.getAmount())
      .reduce(ZERO, BigDecimal::add);
  }

  private boolean isDebit(User user, Transaction transaction) {
    return transaction.getRemitterId().equals(user.getId());
  }

  public List<Transaction> transactions(User user) {
    return repository.transactions(user);
  }
}
