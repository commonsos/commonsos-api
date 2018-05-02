package commonsos;

import javax.persistence.EntityManager;

public abstract class Repository {
  private final EntityManagerService entityManagerService;

  public Repository(EntityManagerService entityManagerService) {
    this.entityManagerService = entityManagerService;
  }

  protected EntityManager em() {
    return entityManagerService.get();
  }
}
