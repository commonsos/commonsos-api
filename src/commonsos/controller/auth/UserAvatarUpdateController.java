package commonsos.controller.auth;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

@Slf4j
public class UserAvatarUpdateController extends Controller {

  @Inject UserService userService;

  @Override public String handle(User user, Request request, Response response) {
    return userService.updateAvatar(user, image(request));
  }
}
