package commonsos.controller.auth;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserPrivateView;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

import static spark.utils.StringUtils.isNotBlank;

public class UserController extends Controller {

  @Inject UserService userService;

  @Override public UserPrivateView handle(User user, Request request, Response response) {
    String requestedUserId = request.params("id");
    if (isNotBlank(requestedUserId)) return userService.privateView(user, requestedUserId);
    return userService.privateView(user);
  }
}
