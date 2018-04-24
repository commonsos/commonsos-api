package commonsos.domain.transaction;

import commonsos.BadRequestException;
import commonsos.DisplayableException;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import commonsos.domain.agreement.Agreement;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.toList;
import static spark.utils.StringUtils.isBlank;

@Singleton
public class TransactionService {
  @Inject AgreementService agreementService;
  @Inject TransactionRepository repository;
  @Inject UserService userService;
  @Inject AdService adService;

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

  public List<TransactionView> transactions(User user) {
    return repository.transactions(user).stream()
      .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
      .map(transaction -> view(user, transaction))
      .collect(toList());
  }

  public TransactionView view(User user, Transaction transaction) {
    UserView remitter = userService.view(transaction.getRemitterId());
    UserView beneficiary = userService.view(transaction.getBeneficiaryId());
    return new TransactionView()
      .setRemitter(remitter)
      .setBeneficiary(beneficiary)
      .setAmount(transaction.getAmount())
      .setDescription(transaction.getDescription())
      .setCreatedAt(transaction.getCreatedAt())
      .setDebit(isDebit(user, transaction));
  }

  public void create(Transaction transaction) {
    repository.create(transaction);
  }

  public void create(User user, TransactionCreateCommand command) {
    if (isBlank(command.getDescription()))  throw new BadRequestException();
    if (ZERO.compareTo(command.getAmount()) > -1)  throw new BadRequestException();
    if (user.getId().equals(command.getBeneficiaryId())) throw new BadRequestException();
    userService.user(command.getBeneficiaryId());

    if (command.getAdId() != null) {
      Ad ad = adService.ad(command.getAdId());
      if (!command.getBeneficiaryId().equals(ad.getCreatedBy())) throw new BadRequestException();
    }
    BigDecimal balance = balance(user);
    if (balance.compareTo(command.getAmount()) < 0) throw new DisplayableException("Not enough funds");
    
    repository.create(new Transaction()
      .setRemitterId(user.getId())
      .setAmount(command.getAmount())
      .setBeneficiaryId(command.getBeneficiaryId())
      .setDescription(command.getDescription())
      .setAdId(command.getAdId())
      .setCreatedAt(OffsetDateTime.now())
    );
  }
}
