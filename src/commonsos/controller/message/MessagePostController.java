package commonsos.controller.message;

import com.google.gson.Gson;
import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.message.MessagePostCommand;
import commonsos.domain.message.MessageService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class MessagePostController extends Controller {

  @Inject Gson gson;
  @Inject MessageService service;

  @Override protected Object handle(User user, Request request, Response response) {
    service.postMessage(user, gson.fromJson(request.body(), MessagePostCommand.class));
    return "";
  }
}
