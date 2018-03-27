package commonsos;

import com.google.gson.Gson;
import com.google.inject.*;
import commonsos.controller.ad.AdAcceptController;
import commonsos.controller.ad.AdCreateController;
import commonsos.controller.ad.AdListController;
import commonsos.controller.agreement.AgreementController;
import commonsos.controller.agreement.AgreementListController;
import commonsos.controller.auth.LoginController;
import commonsos.controller.auth.LogoutController;
import commonsos.controller.transaction.BalanceController;
import commonsos.controller.transaction.ClaimRewardController;
import commonsos.controller.transaction.TransactionListController;
import lombok.extern.slf4j.Slf4j;

import static java.util.Arrays.asList;
import static spark.Spark.*;

@Slf4j
public class Server {
  @Inject private JsonTransformer toJson;

  private void start() {
    Injector injector = initDependencies();
    initRoutes(injector);
  }

  private Injector initDependencies() {
    Module module = new AbstractModule() {
      @Override protected void configure() {
        bind(Gson.class).toProvider(GsonProvider.class);
      }
    };

    Injector injector = Guice.createInjector(module);
    injector.injectMembers(this);
    return injector;
  }

  private void initRoutes(Injector injector) {
    post("/login", injector.getInstance(LoginController.class), toJson);
    post("/logout", injector.getInstance(LogoutController.class), toJson);

    before((request, response) -> log.info(request.pathInfo()));
    before(new AuthenticationFilter(asList("/login")));

    post("/ads", injector.getInstance(AdCreateController.class), toJson);
    post("/ads/:id/accept", injector.getInstance(AdAcceptController.class), toJson);
    get("/ads", injector.getInstance(AdListController.class), toJson);
    get("/agreements", injector.getInstance(AgreementListController.class), toJson);
    get("/agreements/:id", injector.getInstance(AgreementController.class), toJson);
    post("/claim-reward", injector.getInstance(ClaimRewardController.class), toJson);

    get("/balance", injector.getInstance(BalanceController.class), toJson);
    get("/transactions", injector.getInstance(TransactionListController.class), toJson);

    exception(BadRequestException.class, (exception, request, response) -> {
      log.error("Bad request", exception);
      response.status(400);
      response.body("");
    });
    exception(AuthenticationException.class, (exception, request, response) -> {
      log.error("Not authenticated", exception);
      response.status(401);
      response.body("");
    });
    exception(ForbiddenException.class, (exception, request, response) -> {
      log.error("Access denied", exception);
      response.status(403);
      response.body("");
    });
    exception(Exception.class, (exception, request, response) -> {
      log.error("Processing failed", exception);
      response.status(500);
      response.body("");
    });
  }

  public static void main(String[] args) {
    try {
      new Server().start();
    }
    catch (Throwable e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
