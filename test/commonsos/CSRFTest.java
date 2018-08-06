package commonsos;

import org.junit.Test;
import spark.Request;
import spark.Response;
import spark.Session;

import java.util.HashSet;
import java.util.Set;

import static commonsos.CSRF.CSRF_TOKEN_COOKIE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CSRFTest {

  Request request = mock(Request.class);
  Session session = mock(Session.class);
  Response response = mock(Response.class);
  CSRF csrf = spy(new CSRF());

  @Test
  public void setToken() {
    when(request.session()).thenReturn(session);
    doReturn("random value").when(csrf).generateToken();

    csrf.setToken(request, response);

    verify(response).cookie("/", CSRF_TOKEN_COOKIE_NAME, "random value", 3600 * 24 * 31 * 12, false);
    verify(session).attribute(CSRF.CSRF_TOKEN_SESSION_ATTRIBUTE_NAME, "random value");
  }

  @Test
  public void tokenIsRandomLongString() {
    Set<String> tokens = new HashSet<>();
    for (int i = 0; i < 100; i++)
      tokens.add(csrf.generateToken());

    assertThat(tokens).hasSize(100);
    assertThat(tokens.iterator().next().length()).isGreaterThan(20);
  }
}