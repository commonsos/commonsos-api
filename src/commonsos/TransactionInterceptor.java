package commonsos;


import com.google.inject.AbstractModule;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import spark.Route;

import javax.inject.Inject;
import java.lang.reflect.Method;

@Slf4j
public class TransactionInterceptor extends AbstractModule implements MethodInterceptor {
  @Inject EntityManagerService entityManagerService;

  @Override
  protected void configure() {
    requestInjection(this);
    bindInterceptor(Matchers.subclassesOf(Route.class), new HandleMethodMatcher(), this);
  }

  @Override
  public Object invoke(MethodInvocation invocation) throws Throwable {
    try {
      return entityManagerService.runInTransaction(invocation::proceed);
    }
    finally {
      close(entityManagerService);
    }
  }

  private void close(EntityManagerService entityManagerService) {
    try {
      entityManagerService.close();
    }
    catch (Throwable e) {
      log.error("Failed to close entity manager:", e);
    }
  }

  class HandleMethodMatcher extends AbstractMatcher<Method> {
    @Override
    public boolean matches(Method method) {
      return "handle".equals(method.getName()) && !method.isSynthetic();
    }
  }
}