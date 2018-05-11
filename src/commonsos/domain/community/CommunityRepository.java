package commonsos.domain.community;

import commonsos.EntityManagerService;
import commonsos.Repository;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class CommunityRepository extends Repository {

  public CommunityRepository(EntityManagerService entityManagerService) {
    super(entityManagerService);
  }

  public Optional<Community> findById(Long id) {
    return ofNullable(em().find(Community.class, id));
  }

  public List<Community> list() {
    return em().createQuery("FROM Community WHERE tokenContractId IS NOT NULL", Community.class).getResultList();
  }
}
