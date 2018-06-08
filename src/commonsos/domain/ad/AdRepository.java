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

  public List<Ad> ads(Long communityId) {
    return em()
      .createQuery("FROM Ad WHERE communityId = :communityId", Ad.class)
      .setParameter("communityId", communityId).getResultList();
  }

  public List<Ad> ads(Long communityId, String filter) {
    return em()
      .createQuery("FROM Ad WHERE communityId = :communityId " +
        "AND (LOWER(description) LIKE LOWER(:filter) OR LOWER(title) LIKE LOWER(:filter))", Ad.class)
      .setParameter("communityId", communityId)
      .setParameter("filter", "%"+filter+"%")
      .getResultList();
  }

  public Optional<Ad> find(Long id) {
    return ofNullable(em().find(Ad.class, id));
  }

  public Ad update(Ad ad) {
    em().merge(ad);
    return ad;
  }
}
