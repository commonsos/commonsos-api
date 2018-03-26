package commonsos.controller;

import commonsos.domain.user.User;
import spark.Request;
import spark.Response;
import spark.Route;

public abstract class Controller implements Route {

  @Override final public Object handle(Request request, Response response) {
    return handle(request.session().attribute("user"), request, response);
  }

  abstract protected Object handle(User user, Request request, Response response);
}
