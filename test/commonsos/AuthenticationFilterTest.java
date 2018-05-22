package commonsos;

import commonsos.domain.auth.User;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Session;

import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

public class AuthenticationFilterTest {

  Request request = mock(Request.class);
  Session session = mock(Session.class);

  @Before
  public void setUp() throws Exception {
    when(request.session()).thenReturn(session);
  }

 @Test
  public void ignoresExcludedPaths() {
    when(request.pathInfo()).thenReturn("no-auth-check-needed");

    new AuthenticationFilter(singletonList("no-auth-check-needed")).handle(request, null);

    verifyZeroInteractions(session);
  }

  @Test
  public void userAuthenticated() {
    when(session.attribute(USER_SESSION_ATTRIBUTE_NAME)).thenReturn(mock(User.class));

    new AuthenticationFilter(emptyList()).handle(request, null);
  }

  @Test(expected = AuthenticationException.class)
  public void noUserInSession() {
    when(session.attribute(USER_SESSION_ATTRIBUTE_NAME)).thenReturn(null);

    new AuthenticationFilter(emptyList()).handle(request, null);
  }
}