package commonsos;

import commonsos.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import spark.Filter;
import spark.Request;
import spark.Response;

import java.util.List;

import static spark.Spark.halt;

@Slf4j
public class AuthenticationFilter implements Filter {

  private List<String> exclusionPaths;

  public AuthenticationFilter(List<String> exclusionPaths) {
    this.exclusionPaths = exclusionPaths;
  }

  @Override public void handle(Request request, Response response) {
    if (exclusionPaths.contains(request.contextPath())) {
      System.out.println("Ignoring path " + request.contextPath());
      return;
    }

    log.info("Verifying path " + request.contextPath());

    String userId = request.headers("X-UserId");
    if (userId == null) {
      halt(401);
      return;
    }
    request.session().attribute("user", new User().setId(userId));
  }
}
