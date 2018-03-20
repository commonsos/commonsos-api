package commonsos;

import spark.Filter;
import spark.Request;
import spark.Response;

import static spark.Spark.halt;

public class AuthenticationFilter implements Filter {

  @Override public void handle(Request request, Response response) throws Exception {
    String userId = request.headers("X-UserId");
    if (userId == null) {
      halt(401);
      return;
    }
    request.session().attribute("user", new User().setId(userId));
  }
}
