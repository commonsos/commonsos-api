package commonsos.controller;

import commonsos.UserSession;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;

import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;

public abstract class Controller implements Route {

  @Inject UserService userService;

  @Override final public Object handle(Request request, Response response) {
    UserSession session = request.session().attribute(USER_SESSION_ATTRIBUTE_NAME);
    User user = userService.user(session.getUserId());
    return handle(user, request, response);
  }

  abstract protected Object handle(User user, Request request, Response response);
}
