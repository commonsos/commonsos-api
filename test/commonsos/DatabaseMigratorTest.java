package commonsos;

import org.flywaydb.core.Flyway;
import org.junit.Test;

import javax.sql.DataSource;

import static org.mockito.Mockito.*;

public class DatabaseMigratorTest {

  DataSource dataSource = mock(DataSource.class);
  Flyway flyway = mock(Flyway.class);
  EntityManagerService entityManagerService = mock(EntityManagerService.class);

  @Test
  public void migrate() {
    when(entityManagerService.dataSource()).thenReturn(dataSource);

    new DatabaseMigrator(entityManagerService, flyway).execute();

    verify(flyway).setDataSource(dataSource);
    verify(flyway).migrate();
  }
}