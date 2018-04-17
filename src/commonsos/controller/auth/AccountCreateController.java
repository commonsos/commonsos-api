package commonsos.controller.auth;

import com.google.gson.Gson;
import commonsos.domain.auth.AccountCreateCommand;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserPrivateView;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;

public class AccountCreateController implements Route {

  @Inject Gson gson;
  @Inject UserService userService;

  @Override public UserPrivateView handle(Request request, Response response) {
    AccountCreateCommand command = gson.fromJson(request.body(), AccountCreateCommand.class);
    User user = userService.create(command);
    request.session().attribute("user", user);
    return userService.privateView(user);
  }
}
