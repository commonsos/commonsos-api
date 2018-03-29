package commonsos.controller.ad;

import commonsos.controller.Controller;
import commonsos.domain.ad.AdService;
import commonsos.domain.auth.User;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class AdListController extends Controller {
  @Inject AdService service;

  @Override public Object handle(User user, Request request, Response response) {
    return service.all(user);
  }
}
