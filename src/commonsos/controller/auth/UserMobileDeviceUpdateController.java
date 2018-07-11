package commonsos.controller.auth;

import com.google.gson.Gson;
import commonsos.controller.Controller;
import commonsos.domain.auth.MobileDeviceUpdateCommand;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class UserMobileDeviceUpdateController extends Controller {

  @Inject UserService userService;
  @Inject Gson gson;

  @Override protected Object handle(User user, Request request, Response response) {
    MobileDeviceUpdateCommand command = gson.fromJson(request.body(), MobileDeviceUpdateCommand.class);
    userService.updateMobileDevice(user, command);
    return "";
  }
}
