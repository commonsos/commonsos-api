package commonsos.domain.ad;

import commonsos.domain.agreement.AgreementService;
import commonsos.domain.user.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AdService {
  @Inject AdRepository repository;
  @Inject AgreementService agreementService;

  public void create(User user, Ad ad) {
    ad.setCreatedBy(user.getId());
    repository.create(ad);
  }

  public List<Ad> all() {
    return repository.list();
  }

  public Ad accept(User user, String id) {
    Ad ad = repository.find(id).orElseThrow(RuntimeException::new);
    repository.save(ad);
    agreementService.create(user, ad);
    return ad;
  }
}
