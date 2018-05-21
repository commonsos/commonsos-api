package commonsos.controller.auth;

import com.google.gson.Gson;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserPrivateView;
import commonsos.domain.auth.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import java.util.Map;

import static commonsos.CsrfFilter.CSRF_TOKEN_COOKIE_NAME;
import static commonsos.CsrfFilter.CSRF_TOKEN_SESSION_ATTRIBUTE_NAME;
import static java.util.UUID.randomUUID;

public class LoginController implements Route {

  @Inject Gson gson;
  @Inject UserService userService;

  @Override public UserPrivateView handle(Request request, Response response) {
    Map map = gson.fromJson(request.body(), Map.class);

    User user = userService.checkPassword(String.valueOf(map.get("username")), String.valueOf(map.get("password")));
    request.session().attribute("user", user);
    String csrfToken = generateCsrfToken();
    request.session().attribute(CSRF_TOKEN_SESSION_ATTRIBUTE_NAME, csrfToken);
    response.cookie("/", CSRF_TOKEN_COOKIE_NAME, csrfToken, -1, false);
    return userService.privateView(user);
  }

  String generateCsrfToken() {
    return randomUUID().toString();
  }
}
