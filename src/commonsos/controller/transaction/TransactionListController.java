package commonsos.controller.transaction;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.reward.TransactionService;
import spark.Request;
import spark.Response;

public class TransactionListController extends Controller {

  private TransactionService service;

  @Override protected Object handle(User user, Request request, Response response) {
    return service.transactions(user);
  }
}
