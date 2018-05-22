package commonsos;

import commonsos.domain.auth.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;
import spark.Request;
import spark.Response;
import spark.Session;

import static commonsos.LogFilter.X_REQUEST_ID;
import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LogFilterTest {

  LogFilter filter = new LogFilter();
  Request request = mock(Request.class);
  Session session = mock(Session.class);
  Response response = mock(Response.class);

  @Before
  public void setUp() throws Exception {
    when(request.session()).thenReturn(session);
    when(session.id()).thenReturn("12345678901234567890");
  }

  @Test
  public void requestId_usesHeaderValueIfAvailable() throws Exception {
    when(request.headers(X_REQUEST_ID)).thenReturn("apache id");

    filter.handle(request, response);

    assertThat(MDC.get("requestId")).isEqualTo("apache id");
  }

  @Test
  public void requestId_isSequentialValue() throws Exception {
    filter.handle(request, response);
    assertThat(MDC.get("requestId")).isEqualTo("1");

    filter.handle(request, response);
    assertThat(MDC.get("requestId")).isEqualTo("2");

    filter.handle(request, response);
    assertThat(MDC.get("requestId")).isEqualTo("3");
  }

  @Test
  public void userName() throws Exception {
    when(session.attribute(USER_SESSION_ATTRIBUTE_NAME)).thenReturn(new User().setUsername("john"));
    filter.handle(request, response);

    assertThat(MDC.get("username")).isEqualTo("john");
  }

  @Test
  public void userName_notLoggedIn() throws Exception {
    when(session.attribute(USER_SESSION_ATTRIBUTE_NAME)).thenReturn(null);
    filter.handle(request, response);

    assertThat(MDC.get("username")).isEqualTo("");
  }

  @Test
  public void sessionId_truncatesValue() throws Exception {
    when(session.id()).thenReturn("1234567890abcdefghij");

    filter.handle(request, response);

    assertThat(MDC.get("sessionId")).isEqualTo("1234567890");
  }
}