package commonsos;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import spark.HaltException;
import spark.Request;

import static commonsos.CsrfFilter.CSRF_TOKEN_SESSION_ATTRIBUTE_NAME;
import static commonsos.Server.LOGIN_PATH;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CsrfFilterTest {
  @Mock(answer = Answers.RETURNS_DEEP_STUBS) Request request;
  @Mock Logger logger;
  @InjectMocks @Spy CsrfFilter filter;

  @Test
  public void handle() throws Exception {
    when(request.requestMethod()).thenReturn("POST");
    when(request.headers("X-XSRF-TOKEN")).thenReturn("token");
    when(request.session().attribute(CSRF_TOKEN_SESSION_ATTRIBUTE_NAME)).thenReturn("token");

    filter.handle(request, null);
  }

  @Test
  public void handle_tokenIsNotMatching() throws Exception {
    when(request.headers("X-XSRF-TOKEN")).thenReturn("fake token");
    when(request.session().attribute(CSRF_TOKEN_SESSION_ATTRIBUTE_NAME)).thenReturn("token");
    when(request.requestMethod()).thenReturn("POST");

    try {
      filter.handle(request, null);
      fail();
    }
    catch (HaltException e) {
      assertEquals(403, e.statusCode());
      verify(logger).error("CSRF token mismatch! Expected {}, received {}", "token", "fake token");
    }
  }

  @Test
  public void handle_noTokenCheckForGET() throws Exception {
    when(request.requestMethod()).thenReturn("GET");

    filter.handle(request, null);
  }

  @Test
  public void handle_skipTokenCheckForGET() throws Exception {
    when(request.requestMethod()).thenReturn("GET");

    filter.handle(request, null);
  }

  @Test
  public void handle_skipTokenCheckForHEAD() throws Exception {
    when(request.requestMethod()).thenReturn("HEAD");

    filter.handle(request, null);
  }

  @Test
  public void handle_noTokenCheckForLoginPaths() throws Exception {
    when(request.requestMethod()).thenReturn("POST");

    doReturn(true).when(filter).isLogin(request);

    filter.handle(request, null);
  }

  @Test
  public void isLoginPath() throws Exception {
    when(request.pathInfo()).thenReturn(LOGIN_PATH);
    assertTrue(filter.isLogin(request));
  }
}
