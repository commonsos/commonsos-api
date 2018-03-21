package commonsos.domain.agreement;

import commonsos.ForbiddenException;
import commonsos.User;
import commonsos.domain.ad.Ad;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;

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
    return repository.consumedBy(user.getId());
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
    return Base64.getEncoder().encodeToString(("salt"+agreement.getId()).getBytes());
  }
}
