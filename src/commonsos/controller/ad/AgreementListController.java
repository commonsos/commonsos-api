package commonsos.controller.ad;

import commonsos.domain.agreement.AgreementService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;

public class AgreementListController implements Route {
  @Inject AgreementService service;

  @Override public Object handle(Request request, Response response) {
    return service.list(request.headers("X-UserId"));
  }
}
