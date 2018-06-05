package commonsos.controller.ad;

import com.google.gson.Gson;
import commonsos.controller.Controller;
import commonsos.domain.ad.AdCreateCommand;
import commonsos.domain.ad.AdService;
import commonsos.domain.ad.AdView;
import commonsos.domain.auth.User;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class AdCreateController extends Controller {

  @Inject AdService service;
  @Inject Gson gson;

  @Override public AdView handle(User user, Request request, Response response) {
    AdCreateCommand command = gson.fromJson(request.body(), AdCreateCommand.class);
    return service.create(user, command);
  }
}
