package commonsos.controller.ad;

import com.google.gson.Gson;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.inject.Inject;

public class AdCreateController implements Route {

  @Inject AdService service;
  @Inject Gson gson;

  @Override public Object handle(Request request, Response response) throws Exception {
    Ad ad = gson.fromJson(request.body(), Ad.class);
    service.create(ad);
    return "";
  }
}
