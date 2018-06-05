package commonsos.controller;

import commonsos.UserSession;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;

public abstract class Controller implements Route {

  @Inject UserService userService;

  @Override final public Object handle(Request request, Response response) {
    UserSession session = request.session().attribute(USER_SESSION_ATTRIBUTE_NAME);
    User user = userService.user(session.getUserId());
    return handle(user, request, response);
  }

  abstract protected Object handle(User user, Request request, Response response);

  public InputStream image(Request request) {
    String base64 = request.body().replaceFirst("data:image/.*;base64,", "");
    return new ByteArrayInputStream(Base64.getDecoder().decode(base64));
  }
}
