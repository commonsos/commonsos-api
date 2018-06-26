package commonsos;

import com.google.inject.Injector;
import commonsos.domain.auth.User;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static commonsos.LogFilter.USERNAME_MDC_KEY;

@Slf4j
@Singleton
public class JobService {

  @Inject Injector injector;

  private static final int MAXIMUM_POOL_SIZE = 10;
  ThreadPoolExecutor executor;

  @Inject
  public void init() {
    executor = new ThreadPoolExecutor(1, MAXIMUM_POOL_SIZE,
      0L, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>());
  }

  public void submit(User user, Runnable task) {
    injector.injectMembers(task);
    executor.submit(() -> {
      MDC.put(USERNAME_MDC_KEY, user.getUsername());
      try {
        task.run();
      }
      catch (Exception e) {
        log.error("Task execution failed "+ task, e);
      }
    });
  }

  public void execute(Runnable task) {
    injector.injectMembers(task);
    task.run();
  }
}
