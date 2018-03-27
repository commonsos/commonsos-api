package commonsos.domain.transaction;

import commonsos.ForbiddenException;
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
    Agreement agreement = agreementService.findByTransactionData(transactionData);

    if (!user.getId().equals(agreement.getProviderId())) throw new ForbiddenException();
    if (agreement.getRewardClaimedAt() != null) throw new ForbiddenException();

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
    return repository.transactions(user).stream().map(Transaction::getAmount).reduce(ZERO, BigDecimal::add);
  }

  public List<Transaction> transactions(User user) {
    return repository.transactions(user);
  }
}
