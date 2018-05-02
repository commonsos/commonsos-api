package commonsos.domain.ad;

import commonsos.EntityManagerService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Singleton
public class AdRepository {

  @Inject EntityManagerService emService;

  EntityManager em() {
    return emService.get();
  }

  public Ad create(Ad ad) {
    em().persist(ad);
    return ad;
  }

  public List<Ad> list() {
    return em().createQuery("FROM Ad", Ad.class).getResultList();
  }

  public Optional<Ad> find(String id) {
    return ofNullable(em().find(Ad.class, id));
  }
}
