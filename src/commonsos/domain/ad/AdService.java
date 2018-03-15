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

  public List<Ad> list() {
    return repository.list().stream().filter(this::isNotAccepted).collect(toList());
  }

  public void accept(String userId, String id) {
    Ad ad = repository.find(id).orElseThrow(RuntimeException::new);
    ad.setAcceptedBy(userId);
    repository.save(ad);
  }

  boolean isNotAccepted(Ad ad) {
    return  ad.getAcceptedBy() == null;
  }
}
