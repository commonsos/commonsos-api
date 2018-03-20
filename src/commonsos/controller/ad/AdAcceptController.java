package commonsos.controller.ad;

import commonsos.User;
import commonsos.controller.Controller;
import commonsos.domain.ad.AdService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class AdAcceptController extends Controller {
  @Inject AdService service;

  @Override public Object handle(User user,  Request request, Response response) {
    return service.accept(user.getId(), request.params("id"));
  }
}
