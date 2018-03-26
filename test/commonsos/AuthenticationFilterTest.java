package commonsos;

import org.junit.Before;
import org.junit.Test;
import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.Session;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

public class AuthenticationFilterTest {

  Request request = mock(Request.class);
  Session session = mock(Session.class);
  Response response = mock(Response.class);

  @Before
  public void setUp() throws Exception {
    when(request.session()).thenReturn(session);
  }

  @Test
  public void requiresUserHeader() throws Exception {
    when(request.headers("X-UserId")).thenReturn(null);

    try {
      new AuthenticationFilter(emptyList()).handle(request, response);
      failBecauseExceptionWasNotThrown(HaltException.class);
    }
    catch (HaltException e) {
      assertThat(e.statusCode()).isEqualTo(401);
    }
  }

  @Test
  public void storesUserInSession() throws Exception {
    when(request.headers("X-UserId")).thenReturn("user id");

    new AuthenticationFilter(emptyList()).handle(request, null);

    verify(session).attribute("user", new User().setId("user id"));
  }

  @Test
  public void ignoresExcludedPaths() {
    when(request.headers("X-UserId")).thenReturn(null);
    when(request.contextPath()).thenReturn("no-auth-check-needed");

    new AuthenticationFilter(singletonList("no-auth-check-needed")).handle(request, null);

    verifyZeroInteractions(session);
  }
}