package commonsos.controller.auth;

import com.google.gson.Gson;
import commonsos.CSRF;
import commonsos.LogFilter;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserPrivateView;
import commonsos.domain.auth.UserService;
import org.slf4j.MDC;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import javax.inject.Inject;
import java.util.Map;

import static commonsos.CookieSecuringEmbeddedJettyFactory.MAX_SESSION_AGE_IN_SECONDS;

public class LoginController implements Route {

  public static final String USER_SESSION_ATTRIBUTE_NAME = "user";
  @Inject Gson gson;
  @Inject UserService userService;
  @Inject CSRF csrf;

  @Override public UserPrivateView handle(Request request, Response response) {
    Map map = gson.fromJson(request.body(), Map.class);

    User user = userService.checkPassword(String.valueOf(map.get("username")), String.valueOf(map.get("password")));
    Session session = request.session();
    session.attribute(USER_SESSION_ATTRIBUTE_NAME, userService.session(user));
    session.maxInactiveInterval(MAX_SESSION_AGE_IN_SECONDS);
    MDC.put(LogFilter.USERNAME_MDC_KEY, user.getUsername());
    csrf.setToken(request, response);
    return userService.privateView(user);
  }
}
