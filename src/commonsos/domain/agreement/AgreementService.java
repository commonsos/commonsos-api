package commonsos.domain.agreement;

import commonsos.domain.ad.Ad;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.OffsetDateTime;

@Singleton
public class AgreementService {

  @Inject AgreementRepository repository;

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
}
