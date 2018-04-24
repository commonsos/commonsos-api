package commonsos.controller.message;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.message.MessageService;
import commonsos.domain.message.MessageThreadView;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class MessageThreadController extends Controller {
  @Inject MessageService service;

  @Override protected MessageThreadView handle(User user, Request request, Response response) {
    return service.thread(user, request.params("id"));
  }
}