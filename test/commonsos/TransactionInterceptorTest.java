package commonsos;

import commonsos.EntityManagerService.Executable;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransactionInterceptorTest {
  EntityManagerService entityManagerService = mock(EntityManagerService.class);
  @InjectMocks TransactionInterceptor interceptor;

  @Before
  public void setUp() throws Exception {
    interceptor.entityManagerService = entityManagerService;
  }

  @Test
  public void invoke() throws Throwable {
    doAnswer(invocation -> ((Executable)invocation.getArgument(0)).execute()).when(entityManagerService).runInTransaction(any(Executable.class));

    MethodInvocation methodInvocation = mock(MethodInvocation.class);
    when(methodInvocation.proceed()).thenReturn("result");

    assertEquals("result", interceptor.invoke(methodInvocation));

    InOrder inOrder = inOrder(entityManagerService, methodInvocation);
    inOrder.verify(methodInvocation).proceed();
    inOrder.verify(entityManagerService).close();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void invoke_throwsException() throws Throwable {
    doAnswer(invocation -> ((Executable)invocation.getArgument(0)).execute()).when(entityManagerService).runInTransaction(any(Executable.class));

    MethodInvocation methodInvocation = mock(MethodInvocation.class);
    when(methodInvocation.proceed()).thenThrow(new DisplayableException(""));

    try {
      interceptor.invoke(methodInvocation);
      fail();
    }
    catch (DisplayableException e) {}

    InOrder inOrder = inOrder(methodInvocation, entityManagerService);
    inOrder.verify(methodInvocation).proceed();
    inOrder.verify(entityManagerService).close();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void allEntityManagersAreClosedEvenIfOneFails() throws Throwable {
    doAnswer(invocation -> ((Executable)invocation.getArgument(0)).execute()).when(entityManagerService).runInTransaction(any(Executable.class));

    MethodInvocation methodInvocation = mock(MethodInvocation.class);
    when(methodInvocation.proceed()).thenReturn("result");
    doThrow(new RuntimeException()).when(entityManagerService).close();

    interceptor.invoke(methodInvocation);

    InOrder inOrder = inOrder(entityManagerService, methodInvocation);
    inOrder.verify(methodInvocation).proceed();
    inOrder.verify(entityManagerService).close();
    inOrder.verifyNoMoreInteractions();
  }
}
