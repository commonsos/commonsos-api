package commonsos.domain.ad;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AdService {
  @Inject AdRepository repository;

  public void create(String userId, Ad ad) {
    ad.setUserId(userId);
    repository.create(ad);
  }

  public List<Ad> list() {
    return repository.list();
  }
}
