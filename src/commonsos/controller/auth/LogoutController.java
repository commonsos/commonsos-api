package commonsos.controller.auth;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import spark.Request;
import spark.Response;
import spark.Session;

public class LogoutController extends Controller {
  @Override protected Object handle(User user, Request request, Response response) {
    Session session = request.session(false);
    if (session != null) session.invalidate();
    return "";
  }
}
