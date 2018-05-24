package commonsos.controller.message;

import com.google.common.collect.ImmutableMap;
import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.message.MessageService;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.util.Map;

public class MessageThreadUnreadCountController extends Controller {

  @Inject MessageService service;

  @Override protected Map<String, Object> handle(User user, Request request, Response response) {
    return ImmutableMap.of("count", service.unreadMessageThreadCount(user));
  }
}
