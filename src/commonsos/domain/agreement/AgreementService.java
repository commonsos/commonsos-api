package commonsos.domain.agreement;

import commonsos.ForbiddenException;
import commonsos.domain.ad.Ad;
import commonsos.domain.auth.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

@Singleton
public class AgreementService {

  @Inject private AgreementRepository repository;

  public void create(User user, Ad ad) {
    repository.create(instanceFor(user, ad));
  }

  private Agreement instanceFor(User user, Ad ad) {
    return new Agreement()
      .setAdId(ad.getId())
      .setConsumerId(user.getId())
      .setProviderId(ad.getCreatedBy())
      .setLocation(ad.getLocation())
      .setTitle(ad.getTitle())
      .setPoints(ad.getPoints())
      .setDescription(ad.getDescription())
      .setCreatedAt(OffsetDateTime.now());
  }

  public List<Agreement> list(User user) {
    List<Agreement> agreements = repository.consumedBy(user.getId());
    agreements.sort((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));
    return agreements;
  }

  public AgreementViewModel details(User user, String agreementId) {
    Agreement agreement = repository.find(agreementId).orElseThrow(RuntimeException::new);

    if (!user.getId().equals(agreement.getConsumerId())) throw new ForbiddenException();

    return new AgreementViewModel()
      .setId(agreement.getId())
      .setTitle(agreement.getTitle())
      .setDescription(agreement.getDescription())
      .setLocation(agreement.getLocation())
      .setAmount(agreement.getPoints())
      .setTransactionData(transactionData(agreement));
  }

  private String transactionData(Agreement agreement) {
    return Base64.getEncoder().encodeToString(("salt"+agreement.getId()).getBytes(UTF_8));
  }

  public Optional<Agreement> findByTransactionData(String transactionData) {
    try {
      String agreementId = decodeCode(transactionData);
      return repository.find(agreementId);
    }
    catch (Exception e) {
      return Optional.empty();
    }
  }

  private String decodeCode(String transactionData) {
    return new String(Base64.getDecoder().decode(transactionData), UTF_8).replace("salt", "");
  }

  public void rewardClaimed(Agreement agreement) {
    agreement.setRewardClaimedAt(OffsetDateTime.now());
    repository.update(agreement);
  }
}
