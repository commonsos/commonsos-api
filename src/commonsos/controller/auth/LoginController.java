package commonsos.controller.auth;

import com.google.gson.Gson;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserPrivateView;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import java.util.Map;

public class LoginController implements Route {

  @Inject Gson gson;
  @Inject UserService userService;

  @Override public UserPrivateView handle(Request request, Response response) {
    Map map = gson.fromJson(request.body(), Map.class);

    User user = userService.checkPassword(String.valueOf(map.get("username")), String.valueOf(map.get("password")));
    request.session().attribute("user", user);
    return userService.privateView(user);
  }
}
