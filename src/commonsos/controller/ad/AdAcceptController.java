package commonsos.controller.ad;

import commonsos.domain.ad.AdService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;

public class AdAcceptController implements Route {
  @Inject AdService service;

  @Override public Object handle(Request request, Response response) throws Exception {
    return service.accept(request.headers("X-UserId"), request.params("id"));
  }
}
