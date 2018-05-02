package commonsos.domain.ad;

import commonsos.EntityManagerService;
import commonsos.Repository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Singleton
public class AdRepository extends Repository {

  @Inject public AdRepository(EntityManagerService entityManagerService) {
    super(entityManagerService);
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
