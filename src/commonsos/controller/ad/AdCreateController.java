package commonsos.controller.ad;

import com.google.gson.Gson;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import spark.Request;
import spark.Response;
import spark.Route;

public class AdCreateController implements Route {

  private AdService service = AdService.instance;
  private Gson gson = new Gson();

  @Override public Object handle(Request request, Response response) throws Exception {
    Ad ad = gson.fromJson(request.body(), Ad.class);
    service.create(ad);
    return "";
  }
}
