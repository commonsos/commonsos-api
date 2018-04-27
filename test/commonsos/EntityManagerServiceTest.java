package commonsos;

import commonsos.EntityManagerService.Executable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EntityManagerServiceTest {
  @Mock EntityManagerFactory entityManagerFactory;
  @InjectMocks @Spy EntityManagerService entityManagerService = new EntityManagerService() {
    @Override public void init() { }
  };

  @Test
  public void get() throws Exception {
    EntityManager entityManager = mock(EntityManager.class);
    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);

    assertSame(entityManager, entityManagerService.get());
    assertSame(entityManager, entityManagerService.get());

    verify(entityManagerFactory, times(1)).createEntityManager();
  }

  @Test
  public void close() throws Exception {
    EntityManager entityManager = mock(EntityManager.class);
    entityManagerService.em.set(entityManager);

    entityManagerService.close();

    verify(entityManager).close();
    assertNull(entityManagerService.em.get());
  }

  @Test
  public void remove_nullSafe() throws Exception {
    EntityManager entityManager = mock(EntityManager.class);
    entityManagerService.em = spy(entityManagerService.em);

    entityManagerService.close();

    verify(entityManager, never()).close();
    verify(entityManagerService.em, never()).remove();
  }

  private static class MyThread extends Thread {
    public EntityManager entityManager;
    private EntityManagerService entityManagerService;

    public MyThread(EntityManagerService entityManagerService) {
      this.entityManagerService = entityManagerService;
    }

    @Override
    public void run() {
      entityManager = entityManagerService.get();
    }
  }

  @Test
  public void differentThreadsGetDifferentEntityManagers() throws Exception {
    EntityManager entityManager1 = mock(EntityManager.class);
    EntityManager entityManager2 = mock(EntityManager.class);
    when(entityManagerFactory.createEntityManager())
      .thenReturn(entityManager1)
      .thenReturn(entityManager2);

    MyThread thread1 = new MyThread(entityManagerService);
    thread1.start();
    thread1.join();

    MyThread thread2 = new MyThread(entityManagerService);
    thread2.start();
    thread2.join();

    verify(entityManagerFactory, times(2)).createEntityManager();
    assertSame(entityManager1, thread1.entityManager);
    assertSame(entityManager2, thread2.entityManager);
  }

  @Test
  public void transactional() throws Throwable {
    EntityManager entityManager = mock(EntityManager.class);
    entityManagerService.em.set(entityManager);
    EntityTransaction transaction = mock(EntityTransaction.class);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(transaction.isActive()).thenReturn(false).thenReturn(true);

    Executable<String> code = mock(Executable.class);
    when(code.execute()).thenReturn("test");

    assertEquals("test", entityManagerService.runInTransaction(code));

    InOrder inOrder = inOrder(transaction, code);
    inOrder.verify(transaction).begin();
    inOrder.verify(code).execute();
    inOrder.verify(transaction).commit();
    verify(transaction, never()).rollback();
  }

  @Test
  public void transactional_rollbacksInCaseOfRuntimeException() throws Throwable {
    EntityManager entityManager = mock(EntityManager.class);
    entityManagerService.em.set(entityManager);
    EntityTransaction transaction = mock(EntityTransaction.class);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(transaction.isActive()).thenReturn(false).thenReturn(true);

    Executable<String> code = mock(Executable.class);
    RuntimeException runtimeException = new RuntimeException();
    when(code.execute()).thenThrow(runtimeException);

    try {
      entityManagerService.runInTransaction(code);
      fail();
    }
    catch (RuntimeException e) {
      assertSame(runtimeException, e);
      InOrder inOrder = inOrder(transaction, code);
      inOrder.verify(transaction).begin();
      inOrder.verify(code).execute();
      inOrder.verify(transaction).rollback();
      verify(transaction, never()).commit();
    }
  }

  @Test
  public void transactional_rollbacksInCaseOfCheckedException() throws Throwable {
    EntityManager entityManager = mock(EntityManager.class);
    entityManagerService.em.set(entityManager);
    EntityTransaction transaction = mock(EntityTransaction.class);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(transaction.isActive()).thenReturn(false).thenReturn(true);

    Executable<String> code = mock(Executable.class);
    Exception exception = new Exception();
    when(code.execute()).thenThrow(exception);

    try {
      entityManagerService.runInTransaction(code);
      fail();
    }
    catch (Exception e) {
      assertSame(exception, e.getCause());
      InOrder inOrder = inOrder(transaction, code);
      inOrder.verify(transaction).begin();
      inOrder.verify(code).execute();
      inOrder.verify(transaction).rollback();
      verify(transaction, never()).commit();
    }
  }

  @Test
  public void runInTransactionWithoutReturnValue() throws Throwable {
    Runnable runnable = mock(Runnable.class);
    doAnswer(invocation -> ((Executable)invocation.getArgument(0)).execute())
      .when(entityManagerService).runInTransaction(any(Executable.class));

    entityManagerService.runInTransaction(runnable);

    verify(runnable).run();
  }

  @Test
  public void runInTransactionSupportsNestedCalls() throws Throwable {
    EntityManager entityManager = mock(EntityManager.class);
    entityManagerService.em.set(entityManager);
    EntityTransaction transaction = mock(EntityTransaction.class);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(transaction.isActive()).thenReturn(false).thenReturn(true);

    Executable<String> innerCode = mock(Executable.class);
    Executable<String> code = spy(new Executable<String>() {
      @Override
      public String execute() throws Throwable {
        return entityManagerService.runInTransaction(innerCode);
      }
    });

    entityManagerService.runInTransaction(code);

    InOrder inOrder = inOrder(transaction, innerCode, code);
    inOrder.verify(transaction).begin();
    inOrder.verify(code).execute();
    inOrder.verify(innerCode).execute();
    inOrder.verify(transaction).commit();
    verify(transaction, never()).rollback();
  }

  @Test
  public void defaultSchema() throws Exception {
    when(entityManagerFactory.getProperties()).thenReturn(Collections.singletonMap("hibernate.default_schema", "schema"));

    assertEquals("schema", entityManagerService.defaultSchema());
  }
}
