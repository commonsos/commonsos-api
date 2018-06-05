package commonsos.controller.ad;

import commonsos.controller.Controller;
import commonsos.domain.ad.AdPhotoUpdateCommand;
import commonsos.domain.ad.AdService;
import commonsos.domain.auth.User;
import spark.Request;
import spark.Response;

import javax.inject.Inject;

public class AdPhotoUpdateController extends Controller {
  @Inject AdService service;

  @Override protected String handle(User user, Request request, Response response) {
    long adId = Long.parseLong(request.params("id"));
    return service.updatePhoto(user, new AdPhotoUpdateCommand().setAdId(adId).setPhoto(image(request)));
  }
}
