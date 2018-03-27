package commonsos.controller.transaction;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.transaction.TransactionService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class TransactionListController extends Controller {

  @Inject private TransactionService service;

  @Override protected Object handle(User user, Request request, Response response) {
    return service.transactions(user);
  }
}
