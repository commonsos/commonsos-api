package commonsos.domain.community;

import commonsos.EntityManagerService;
import commonsos.Repository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Singleton
public class CommunityRepository extends Repository {

  @Inject
  public CommunityRepository(EntityManagerService entityManagerService) {
    super(entityManagerService);
  }

  public Optional<Community> findById(Long id) {
    return ofNullable(em().find(Community.class, id));
  }

  public List<Community> list() {
    return em().createQuery("FROM Community WHERE tokenContractId IS NOT NULL", Community.class).getResultList();
  }

  public Community create(Community community) {
    em().persist(community);
    return community;
  }
}
