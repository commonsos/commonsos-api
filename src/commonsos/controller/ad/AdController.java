package commonsos.controller.ad;

import commonsos.controller.Controller;
import commonsos.domain.ad.AdService;
import commonsos.domain.ad.AdView;
import commonsos.domain.auth.User;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

import static java.lang.Long.parseLong;

public class AdController extends Controller {
  @Inject AdService service;

  @Override public AdView handle(User user, Request request, Response response) {
    return service.view(user, parseLong(request.params("id")));
  }
}
