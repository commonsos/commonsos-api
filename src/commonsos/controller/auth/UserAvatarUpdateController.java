package commonsos.controller.auth;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Slf4j
public class UserAvatarUpdateController extends Controller {

  @Inject UserService userService;

  @Override public String handle(User user, Request request, Response response) {
    return userService.updateAvatar(user, image(request));
  }

  InputStream image(Request request) {
    String base64 = request.body().replaceFirst("data:image/.*;base64,", "");
    return new ByteArrayInputStream(Base64.getDecoder().decode(base64));
  }
}
