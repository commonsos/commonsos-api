package commonsos.controller.agreement;

import commonsos.controller.Controller;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.agreement.AgreementView;
import commonsos.domain.auth.User;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class AgreementController extends Controller {

  @Inject AgreementService service;

  @Override public AgreementView handle(User user, Request request, Response response) {
    return service.details(user, request.params("id"));
  }
}
