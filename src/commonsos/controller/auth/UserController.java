package commonsos.controller.auth;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

import static spark.utils.StringUtils.isNotBlank;

public class UserController extends Controller {

  @Inject UserService userService;

  @Override public Object handle(User user, Request request, Response response) {
    String requestedUserId = request.params("id");
    if (isNotBlank(requestedUserId)) {
      return user.isAdmin() ? userService.privateView(user, requestedUserId) : userService.view(requestedUserId);
    }
    return userService.privateView(user);
  }
}
