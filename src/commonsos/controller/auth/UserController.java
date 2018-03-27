package commonsos.controller.auth;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class UserController extends Controller {

  @Inject UserService userService;

  @Override public UserView handle(User user, Request request, Response response) {
    return userService.view(user);
  }
}
