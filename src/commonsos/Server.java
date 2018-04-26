package commonsos;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.inject.*;
import commonsos.controller.ad.*;
import commonsos.controller.admin.UserSearchController;
import commonsos.controller.agreement.AgreementController;
import commonsos.controller.agreement.AgreementListController;
import commonsos.controller.auth.AccountCreateController;
import commonsos.controller.auth.LoginController;
import commonsos.controller.auth.LogoutController;
import commonsos.controller.auth.UserController;
import commonsos.controller.message.*;
import commonsos.controller.transaction.BalanceController;
import commonsos.controller.transaction.ClaimRewardController;
import commonsos.controller.transaction.TransactionCreateController;
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
    injector.getInstance(DemoData.class).install();
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
    post("/create-account", injector.getInstance(AccountCreateController.class), toJson);
    post("/logout", injector.getInstance(LogoutController.class), toJson);
    get("/user", injector.getInstance(UserController.class), toJson);
    get("/users/:id", injector.getInstance(UserController.class), toJson);
    get("/users", injector.getInstance(UserSearchController.class), toJson);

    before((request, response) -> log.info(request.pathInfo()));
    before(new AuthenticationFilter(asList("/login", "/create-account")));

    post("/ads", injector.getInstance(AdCreateController.class), toJson);
    post("/ads/:id/accept", injector.getInstance(AdAcceptController.class), toJson);
    get("/ads", injector.getInstance(AdListController.class), toJson);
    get("/ads/:id", injector.getInstance(AdController.class), toJson);
    get("/my-ads", injector.getInstance(MyAdsController.class), toJson);
    get("/agreements", injector.getInstance(AgreementListController.class), toJson);
    get("/agreements/:id", injector.getInstance(AgreementController.class), toJson);
    post("/claim-reward", injector.getInstance(ClaimRewardController.class), toJson);

    get("/balance", injector.getInstance(BalanceController.class), toJson);
    get("/transactions", injector.getInstance(TransactionListController.class), toJson);
    post("/transactions", injector.getInstance(TransactionCreateController.class), toJson);

    post("/message-threads/for-ad/:adId", injector.getInstance(MessageThreadForAdController.class), toJson);
    get("/message-threads/:id", injector.getInstance(MessageThreadController.class), toJson);
    get("/message-threads", injector.getInstance(MessageThreadListController.class), toJson);
    post("/message-threads/:id/messages", injector.getInstance(MessagePostController.class), toJson);
    get("/message-threads/:id/messages", injector.getInstance(MessageListController.class), toJson);

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
    exception(DisplayableException.class, (exception, request, response) -> {
      log.error("Displayable error", exception);
      response.status(468);
      response.body(toJson.render(ImmutableMap.of("key", exception.getMessage())));
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
