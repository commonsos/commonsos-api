package commonsos.controller.community;

import commonsos.domain.community.CommunityService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;
import java.util.List;

public class CommunityListController implements Route {

  @Inject CommunityService service;

  @Override public List<CommunityView> handle(Request request, Response response) {
    return service.list();
  }
}
