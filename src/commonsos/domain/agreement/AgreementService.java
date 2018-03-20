package commonsos.domain.agreement;

import commonsos.ForbiddenException;
import commonsos.domain.ad.Ad;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;
import java.util.List;

@Singleton
public class AgreementService {

  @Inject private AgreementRepository repository;

  public void create(String userId, Ad ad) {
    repository.create(instanceFor(userId, ad));
  }

  private Agreement instanceFor(String userId, Ad ad) {
    return new Agreement()
      .setAdId(ad.getId())
      .setConsumerId(userId)
      .setProviderId(ad.getCreatedBy())
      .setLocation(ad.getLocation())
      .setTitle(ad.getTitle())
      .setPoints(ad.getPoints())
      .setDescription(ad.getDescription())
      .setCreatedAt(OffsetDateTime.now());
  }

  public List<Agreement> list(String userId) {
    return repository.consumedBy(userId);
  }

  public AgreementViewModel details(String userId, String agreementId) {
    Agreement agreement = repository.find(agreementId).orElseThrow(RuntimeException::new);

    if (!userId.equals(agreement.getConsumerId())) throw new ForbiddenException();

    return new AgreementViewModel()
      .setId(agreementId)
      .setTitle(agreement.getTitle())
      .setDescription(agreement.getDescription())
      .setLocation(agreement.getLocation())
      .setAmount(agreement.getPoints())
      .setTransactionData(String.format("amount:%s|from:%s|to:%s", agreement.getPoints(), agreement.getConsumerId(), agreement.getProviderId()));
  }
}
