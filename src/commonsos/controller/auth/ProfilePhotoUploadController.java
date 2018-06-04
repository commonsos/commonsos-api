package commonsos.controller.auth;

import commonsos.controller.Controller;
import commonsos.domain.auth.ImageService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.Base64;

@Slf4j
public class ProfilePhotoUploadController extends Controller {

  @Inject ImageService imageService;
  @Inject UserService userService;

  @Override public Object handle(User user, Request request, Response response) {
    byte[] bytes = decodeImage(request);

    String url = imageService.upload(new ByteArrayInputStream(bytes));
    userService.setAvatar(user, url);
    return "";
  }

  byte[] decodeImage(Request request) {
    String base64 = request.body().replaceFirst("data:image/.*;base64,", "");
    return Base64.getDecoder().decode(base64);
  }
}