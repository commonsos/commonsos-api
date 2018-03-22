package commonsos.domain.agreement;

import commonsos.BadRequestException;
import commonsos.ForbiddenException;
import commonsos.User;
import commonsos.domain.ad.Ad;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

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
    Collections.sort(agreements, (a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));
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

  public Agreement findByTransactionData(String transactionData) {
    String agreementId = new String(Base64.getDecoder().decode(transactionData), UTF_8).replace("salt", "");
    return repository.find(agreementId).orElseThrow(BadRequestException::new);
  }

  public void rewardClaimed(Agreement agreement) {
    agreement.setRewardClaimedAt(OffsetDateTime.now());
    repository.update(agreement);
  }
}
