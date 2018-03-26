package commonsos.controller.agreement;

import commonsos.controller.Controller;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.agreement.AgreementViewModel;
import commonsos.domain.user.User;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class AgreementController extends Controller {

  @Inject AgreementService service;

  @Override public AgreementViewModel handle(User user, Request request, Response response) {
    return service.details(user, request.params("id"));
  }
}
