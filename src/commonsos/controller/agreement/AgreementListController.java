package commonsos.controller.agreement;

import commonsos.controller.Controller;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.auth.User;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class AgreementListController extends Controller {
  @Inject AgreementService service;

  @Override public Object handle(User user, Request request, Response response) {
    return service.list(user);
  }
}
