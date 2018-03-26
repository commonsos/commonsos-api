package commonsos.controller.reward;

import com.google.gson.Gson;
import commonsos.BadRequestException;
import commonsos.controller.Controller;
import commonsos.domain.reward.TransactionService;
import commonsos.domain.auth.User;
import spark.Request;
import spark.Response;

import javax.inject.Inject;
import java.util.Map;

public class ClaimRewardController extends Controller {

  @Inject Gson gson;
  @Inject TransactionService service;

  @Override protected Object handle(User user, Request request, Response response) {
    Map json = gson.fromJson(request.body(), Map.class);
    if (json == null) throw new BadRequestException();
    return service.claim(user, String.valueOf(json.getOrDefault("code", "")));
  }
}
