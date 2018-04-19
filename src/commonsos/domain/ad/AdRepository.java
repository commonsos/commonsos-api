package commonsos.domain.ad;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class AdRepository {

  List<Ad> ads = new ArrayList<>();

  public Ad create(Ad ad) {
    ad.setId(String.valueOf(ads.size()));
    ads.add(ad);
    return ad;
  }

  public List<Ad> list() {
    return ads;
  }

  public Optional<Ad> find(String id) {
    return ads.stream().filter(a -> a.getId().equals(id)).findAny();
  }

  public void save(Ad ad) {
    // changes made in 'stored' objects immediately reflect in repository due to shared object reference
  }
}
