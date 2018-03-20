package commonsos;

import org.junit.Test;
import spark.HaltException;
import spark.Request;
import spark.Response;
import spark.Session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

public class AuthenticationFilterTest {

  @Test
  public void requiresUserHeader() throws Exception {
    Request request = mock(Request.class);
    when(request.headers("X-UserId")).thenReturn(null);

    Response response = mock(Response.class);

    try {
      new AuthenticationFilter().handle(request, response);
      failBecauseExceptionWasNotThrown(HaltException.class);
    }
    catch (HaltException e) {
      assertThat(e.statusCode()).isEqualTo(401);
    }
  }

  @Test
  public void storesUserInSession() throws Exception {
    Request request = mock(Request.class);
    when(request.headers("X-UserId")).thenReturn("user id");

    Session session = mock(Session.class);
    when(request.session()).thenReturn(session);

    new AuthenticationFilter().handle(request, null);

    verify(session).attribute("user", new User().setId("user id"));
  }
}