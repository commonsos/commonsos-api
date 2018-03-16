package commonsos.domain.ad;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AdService {
  @Inject AdRepository repository;

  public void create(String userId, Ad ad) {
    ad.setCreatedBy(userId);
    repository.create(ad);
  }

  public List<Ad> list() {
    return repository.list();
  }

  public Ad accept(String userId, String id) {
    Ad ad = repository.find(id).orElseThrow(RuntimeException::new);
    ad.setAcceptedBy(userId);
    repository.save(ad);
    return ad;
  }

}
