package commonsos.controller.auth;

import com.google.gson.Gson;
import commonsos.CSRF;
import commonsos.LogFilter;
import commonsos.UserSession;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserPrivateView;
import commonsos.domain.auth.UserService;
import org.slf4j.MDC;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import java.util.Map;

public class LoginController implements Route {

  public static final String USER_SESSION_ATTRIBUTE_NAME = "user";
  @Inject Gson gson;
  @Inject UserService userService;
  @Inject CSRF csrf;

  @Override public UserPrivateView handle(Request request, Response response) {
    Map map = gson.fromJson(request.body(), Map.class);

    User user = userService.checkPassword(String.valueOf(map.get("username")), String.valueOf(map.get("password")));
    UserSession userSession = new UserSession().setUserId(user.getId()).setUsername(user.getUsername());
    request.session().attribute(USER_SESSION_ATTRIBUTE_NAME, userSession);
    MDC.put(LogFilter.USERNAME_MDC_KEY, user.getUsername());
    csrf.setToken(request, response);
    return userService.privateView(user);
  }
}
