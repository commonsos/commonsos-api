package commonsos.controller.ad;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.message.MessageService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class MessageThreadWithUserController extends Controller {
  @Inject MessageService service;

  @Override protected Object handle(User user, Request request, Response response) {
    return service.threadWithUser(user, Long.parseLong(request.params("userId")));
  }
}