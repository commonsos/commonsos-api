package commonsos.controller.auth;

import commonsos.controller.Controller;
import commonsos.domain.auth.ImageService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.util.Base64;

import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;

@Slf4j
public class ProfilePhotoUploadController extends Controller {

  @Inject ImageService imageService;
  @Inject UserService userService;

  @Override public Object handle(User user, Request request, Response response) {
    byte[] bytes = decodeImage(request);

    String url = imageService.uploadImage(bytes);
    userService.setAvatar(user, url);

    request.session().attribute(USER_SESSION_ATTRIBUTE_NAME, user);
    return "";
  }

  byte[] decodeImage(Request request) {
    String base64 = request.body().replaceFirst("data:image/.*;base64,", "");
    return Base64.getDecoder().decode(base64);
  }
}
