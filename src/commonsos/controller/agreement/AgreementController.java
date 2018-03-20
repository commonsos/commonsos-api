package commonsos.controller.agreement;

import commonsos.domain.agreement.AgreementService;
import commonsos.domain.agreement.AgreementViewModel;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;

public class AgreementController implements Route {

  @Inject AgreementService service;

  @Override public AgreementViewModel handle(Request request, Response response) {
    return service.details(request.headers("X-UserId"), request.params("id"));
  }
}
