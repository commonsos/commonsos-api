package commonsos.domain.ad;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AdService {
  @Inject AdRepository repository;

  public void create(Ad ad) {
    repository.create(ad);
  }

  public List<Ad> list() {
    return repository.list();
  }
}
