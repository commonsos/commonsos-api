package commonsos;

import org.flywaydb.core.Flyway;
import org.hibernate.internal.SessionImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class DBTest {
  private static Path dumpFilePath;
  private static EntityManagerFactory entityManagerFactory;
  protected static EntityManagerService entityManagerService = new TestEntityManagerService();

  protected EntityManager em() {
    return entityManagerService.get();
  }

  @BeforeClass
  public static void initDb() {
    Map<String, String> config = new HashMap<>();
    config.put("hibernate.connection.url", "jdbc:h2:mem:commonsos;MODE=PostgreSQL");
    config.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    config.put("hibernate.connection.driver_class", "org.h2.Driver");
    entityManagerFactory = Persistence.createEntityManagerFactory("commonsos", config);
    entityManagerService.entityManagerFactory = entityManagerFactory;

    new DatabaseMigrator(entityManagerService, new Flyway()).execute();

    dumpFilePath = createTempFile();
    executeSQL("script drop to '" + dumpFilePath + "'", entityManagerFactory);
  }

  @AfterClass
  public static void deleteDumpFile() throws IOException {
    Files.delete(dumpFilePath);
  }

  @Before
  public void setUp() throws Exception {
    executeSQL("runscript from '" + dumpFilePath + "'", entityManagerFactory);
  }

  @After
  public void tearDown() {
    executeSQL("DROP ALL OBJECTS", entityManagerFactory);
    entityManagerService.close();
  }

  protected static void executeSQL(String sql, EntityManagerFactory entityManagerFactory) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    transaction.begin();
    Connection connection = ((SessionImpl) entityManager).connection();
    try {
      connection.createStatement().execute(sql);
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    transaction.commit();
  }

  protected static Path createTempFile() {
    try {
      return Files.createTempFile("", "");
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  protected <T> T inTransaction(EntityManagerService.Executable<T> code) {
    try {
      return entityManagerService.runInTransaction(code);
    }
    finally {
      entityManagerService.close();
    }
  }

  protected void inTransaction(Runnable code) {
    inTransaction(() -> {
      code.run();
      return null;
    });
  }

  public static class TestEntityManagerService extends EntityManagerService {
    @Override public void init() {
    }
  }
}


