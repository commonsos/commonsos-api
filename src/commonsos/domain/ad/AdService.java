package commonsos.domain.ad;

import commonsos.domain.agreement.AgreementService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AdService {
  @Inject AdRepository repository;
  @Inject AgreementService agreementService;

  public void create(String userId, Ad ad) {
    ad.setCreatedBy(userId);
    repository.create(ad);
  }

  public List<Ad> all() {
    return repository.list();
  }

  public Ad accept(String userId, String id) {
    Ad ad = repository.find(id).orElseThrow(RuntimeException::new);
    repository.save(ad);
    agreementService.create(userId, ad);
    return ad;
  }
}
