package commonsos.controller.auth;

import org.junit.Test;
import spark.Request;
import spark.Session;

import static org.mockito.Mockito.*;

public class LogoutControllerTest {

  Request request = mock(Request.class);
  Session session = mock(Session.class);

  @Test
  public void logout() {
    when(request.session(false)).thenReturn(session);

    new LogoutController().handle(request, null);

    verify(session).invalidate();
  }

  @Test
  public void logout_sessionNotPresent() {
    when(request.session(false)).thenReturn(null);

    new LogoutController().handle(request, null);
  }
}