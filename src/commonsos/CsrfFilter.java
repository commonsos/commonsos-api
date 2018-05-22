package commonsos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Filter;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static spark.Spark.halt;

public class CsrfFilter implements Filter {
  public static final String CSRF_TOKEN_COOKIE_NAME = "XSRF-TOKEN";
  public static final String CSRF_TOKEN_HEADER_NAME = "X-XSRF-TOKEN";

  public static final String CSRF_TOKEN_SESSION_ATTRIBUTE_NAME = "XSRF-TOKEN";
  Logger logger = LoggerFactory.getLogger(this.getClass());
  private List<String> exclusionPaths;

  public CsrfFilter(List<String> exclusionPaths) {
    this.exclusionPaths = exclusionPaths;
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    if (asList("GET", "HEAD").contains(request.requestMethod().toUpperCase())) {
      return;
    }

    if (exclusionPaths.contains(request.pathInfo())) return;

    String received = request.headers(CSRF_TOKEN_HEADER_NAME);
    String expected = request.session().attribute(CSRF_TOKEN_SESSION_ATTRIBUTE_NAME);

    if (!Objects.equals(expected, received)) {
      logger.error("CSRF token mismatch! Expected {}, received {}", expected, received);
      halt(403);
    }
  }
}
