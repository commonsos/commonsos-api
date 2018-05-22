package commonsos;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import spark.HaltException;
import spark.Request;

import static commonsos.CsrfFilter.CSRF_TOKEN_SESSION_ATTRIBUTE_NAME;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CsrfFilterTest {
  @Mock(answer = Answers.RETURNS_DEEP_STUBS) Request request;
  @Mock Logger logger;
  @InjectMocks CsrfFilter filter = new CsrfFilter(asList("/no-csrf-check"));

  @Test
  public void handle() throws Exception {
    when(request.requestMethod()).thenReturn("POST");
    when(request.headers("X-XSRF-TOKEN")).thenReturn("token");
    when(request.pathInfo()).thenReturn("/path");
    when(request.session().attribute(CSRF_TOKEN_SESSION_ATTRIBUTE_NAME)).thenReturn("token");

    filter.handle(request, null);
  }

  @Test
  public void handle_tokenIsNotMatching() throws Exception {
    when(request.headers("X-XSRF-TOKEN")).thenReturn("fake token");
    when(request.session().attribute(CSRF_TOKEN_SESSION_ATTRIBUTE_NAME)).thenReturn("token");
    when(request.requestMethod()).thenReturn("POST");
    when(request.pathInfo()).thenReturn("/path");

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
  public void handle_noTokenCheckForExclusionPaths() throws Exception {
    when(request.requestMethod()).thenReturn("POST");
    when(request.pathInfo()).thenReturn("/no-csrf-check");
    when(request.session().attribute(CSRF_TOKEN_SESSION_ATTRIBUTE_NAME)).thenReturn("token");

    filter.handle(request, null);
  }
}
