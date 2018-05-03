package commonsos;

import org.flywaydb.core.Flyway;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseMigrator {

  private final EntityManagerService entityManagerService;
  private final Flyway flyway;

  @Inject
  public DatabaseMigrator(EntityManagerService entityManagerService, Flyway flyway) {
    this.entityManagerService = entityManagerService;
    this.flyway = flyway;
  }

  public void execute() {
    flyway.setDataSource(entityManagerService.dataSource());
    flyway.migrate();
  }
}
