package commonsos;

import com.google.inject.Injector;
import commonsos.domain.auth.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.MDC;

import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceTest {

  @InjectMocks JobService service;
  @Mock ThreadPoolExecutor executor;
  @Mock Injector injector;

  @Test
  public void submit() {
    User user = new User().setUsername("john");
    Runnable task = mock(Runnable.class);
    when(executor.submit(any(Runnable.class))).thenAnswer(invocation -> {
      ((Runnable) invocation.getArgument(0)).run();
      return null;
    });

    service.submit(user, task);

    assertThat(MDC.get("username")).isEqualTo("john");
    verify(task).run();
    verify(injector).injectMembers(task);
  }
}