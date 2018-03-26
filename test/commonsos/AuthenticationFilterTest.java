package commonsos;

import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
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
  UserService userService = mock(UserService.class);

  @Before
  public void setUp() throws Exception {
    when(request.session()).thenReturn(session);
  }

  @Test
  public void requiresUserHeader() throws Exception {
    when(request.headers("X-UserId")).thenReturn(null);

    try {
      new AuthenticationFilter(emptyList(), userService).handle(request, response);
      failBecauseExceptionWasNotThrown(HaltException.class);
    }
    catch (HaltException e) {
      assertThat(e.statusCode()).isEqualTo(401);
    }
  }

  @Test
  public void storesUserInSession() throws Exception {
    when(request.headers("X-UserId")).thenReturn("auth token");
    User user = new User();
    when(userService.userByToken("auth token")).thenReturn(user);

    new AuthenticationFilter(emptyList(), userService).handle(request, null);

    verify(session).attribute("user", user);
  }

  @Test
  public void ignoresExcludedPaths() {
    when(request.headers("X-UserId")).thenReturn(null);
    when(request.contextPath()).thenReturn("no-auth-check-needed");

    new AuthenticationFilter(singletonList("no-auth-check-needed"), userService).handle(request, null);

    verifyZeroInteractions(session);
  }
}