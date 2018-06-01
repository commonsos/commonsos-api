package commonsos.controller;

import commonsos.domain.auth.ImageService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ImageController implements Route {

  @Inject ImageService imageService;

  @Override public Object handle(Request request, Response response) throws IOException {
    byte[] image = imageService.image(request.uri());

    response.type("application/jpeg");
    response.header("Content-Length", String.valueOf(image.length));

    HttpServletResponse raw = response.raw();
    raw.getOutputStream().write(image);
    raw.getOutputStream().flush();
    raw.getOutputStream().close();
    return raw;

  }
}
