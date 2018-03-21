package commonsos.domain.reward;

import commonsos.ForbiddenException;
import commonsos.User;
import commonsos.domain.agreement.Agreement;
import commonsos.domain.agreement.AgreementService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;

@Singleton
public class TransactionService {
  @Inject AgreementService agreementService;
  @Inject TransactionRepository repository;

  public void claim(User user, String transactionData) {
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
  }
}
