package commonsos.controller.ad;

import commonsos.domain.ad.AdService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;

public class MyAdListController implements Route {
  @Inject AdService service;

  @Override public Object handle(Request request, Response response) throws Exception {
    return service.acceptedBy(request.headers("X-UserId"));
  }
}
