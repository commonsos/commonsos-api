package commonsos.controller.ad;

import commonsos.domain.ad.AdService;
import spark.Request;
import spark.Response;
import spark.Route;

public class AdListController implements Route {
  private AdService service = AdService.instance;

  @Override public Object handle(Request request, Response response) throws Exception {
    return service.list();
  }
}
