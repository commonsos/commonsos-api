package commonsos;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.sql.DataSource;

@Singleton
public class EntityManagerService {
  private Logger logger = LoggerFactory.getLogger(EntityManagerService.class);

  EntityManagerFactory entityManagerFactory;
  ThreadLocal<EntityManager> em = new ThreadLocal<>();

  @Inject
  public void init() {
    this.entityManagerFactory = Persistence.createEntityManagerFactory("commonsos");
  }

  public EntityManager get() {
    if (em.get() == null) {
      em.set(entityManagerFactory.createEntityManager());
    }
    return em.get();
  }

  public void close() {
    if (em.get() != null) {
      em.get().close();
      em.remove();
    }
  }

  public String defaultSchema() {
    return (String) entityManagerFactory.getProperties().get("hibernate.default_schema");
  }

  public DataSource dataSource() {
    ConnectionProvider connectionProvider = ((SessionFactoryImpl) entityManagerFactory).getServiceRegistry().getService(ConnectionProvider.class);
    return connectionProvider.unwrap(DataSource.class);
  }

  @FunctionalInterface
  public interface Executable<V> {
    V execute() throws Throwable;
  }

  public <T> T runInTransaction(Executable<T> code) {
    EntityTransaction transaction = get().getTransaction();
    try {
      if (!transaction.isActive()) transaction.begin();
      T result = code.execute();
      if (transaction.isActive()) transaction.commit();
      return result;
    }
    catch (RuntimeException e) {
      if (transaction.isActive()) rollback(transaction);
      throw e;
    }
    catch (Throwable e) {
      if (transaction.isActive()) rollback(transaction);
      throw new RuntimeException(e);
    }
  }

  private void rollback(EntityTransaction transaction) {
    try {
      transaction.rollback();
    }
    catch (Exception e) {
      logger.error("Could not rollback the transaction", e);
    }
  }

  public void runInTransaction(Runnable code) {
    runInTransaction((Executable<Void>) () -> {
      code.run();
      return null;
    });
  }
}
