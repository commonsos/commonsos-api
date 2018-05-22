package commonsos.controller;

import commonsos.domain.auth.User;
import spark.Request;
import spark.Response;
import spark.Route;

import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;

public abstract class Controller implements Route {

  @Override final public Object handle(Request request, Response response) {
    return handle(request.session().attribute(USER_SESSION_ATTRIBUTE_NAME), request, response);
  }

  abstract protected Object handle(User user, Request request, Response response);
}
