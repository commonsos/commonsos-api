package commonsos.controller.admin;

import commonsos.controller.Controller;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.util.List;

public class UserSearchController extends Controller {

  @Inject UserService service;

  @Override public List<UserView> handle(User user, Request request, Response response) {
    return service.searchUsers(user, request.queryParams("q"));
  }
}
