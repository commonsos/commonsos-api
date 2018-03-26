package commonsos.controller.ad;

import com.google.gson.Gson;
import commonsos.controller.Controller;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import commonsos.domain.user.User;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class AdCreateController extends Controller {

  @Inject AdService service;
  @Inject Gson gson;

  @Override public Object handle(User user, Request request, Response response) {
    Ad ad = gson.fromJson(request.body(), Ad.class);
    service.create(user, ad);
    return "";
  }
}
