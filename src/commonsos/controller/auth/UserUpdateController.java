package commonsos.controller.auth;

import com.google.gson.Gson;
import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserPrivateView;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserUpdateCommand;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class UserUpdateController extends Controller {

  @Inject UserService userService;
  @Inject Gson gson;

  @Override protected UserPrivateView handle(User user, Request request, Response response) {
    UserUpdateCommand command = gson.fromJson(request.body(), UserUpdateCommand.class);
    User updatedUser = userService.updateUser(user, command);
    return userService.privateView(updatedUser);
  }
}
