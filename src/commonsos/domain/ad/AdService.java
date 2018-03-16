package commonsos.domain.ad;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Singleton
public class AdService {
  @Inject AdRepository repository;

  public void create(String userId, Ad ad) {
    ad.setCreatedBy(userId);
    repository.create(ad);
  }

  public List<Ad> all() {
    return repository.list();
  }

  public List<Ad> acceptedBy(String userId) {
    return repository.list().stream().filter(a -> userId.equals(a.getAcceptedBy())).collect(toList());
  }

  public Ad accept(String userId, String id) {
    Ad ad = repository.find(id).orElseThrow(RuntimeException::new);
    ad.setAcceptedBy(userId);
    repository.save(ad);
    return ad;
  }
}
