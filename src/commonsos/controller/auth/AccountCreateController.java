package commonsos.controller.auth;

import com.google.gson.Gson;
import commonsos.CSRF;
import commonsos.domain.auth.AccountCreateCommand;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserPrivateView;
import commonsos.domain.auth.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;

import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;

public class AccountCreateController implements Route {

  @Inject Gson gson;
  @Inject UserService userService;
  @Inject CSRF csrf;

  @Override public UserPrivateView handle(Request request, Response response) {
    AccountCreateCommand command = gson.fromJson(request.body(), AccountCreateCommand.class);
    User user = userService.create(command);
    request.session().attribute(USER_SESSION_ATTRIBUTE_NAME, userService.session(user));
    csrf.setToken(request, response);
    return userService.privateView(user);
  }
}
