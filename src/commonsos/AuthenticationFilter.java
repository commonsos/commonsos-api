package commonsos;

import commonsos.domain.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import spark.Filter;
import spark.Request;
import spark.Response;

import java.util.List;

@Slf4j
public class AuthenticationFilter implements Filter {

  private UserService userService;
  private List<String> exclusionPaths;

  public AuthenticationFilter(List<String> exclusionPaths, UserService userService) {
    this.userService = userService;
    this.exclusionPaths = exclusionPaths;
  }

  @Override public void handle(Request request, Response response) {
    if (exclusionPaths.contains(request.pathInfo())) return;

    String token = request.headers("X-UserId");
    if (token == null) throw new AuthenticationException();
    request.session().attribute("user", userService.userByToken(token));
  }
}
