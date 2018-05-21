package commonsos;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.inject.*;
import commonsos.controller.ad.AdController;
import commonsos.controller.ad.AdCreateController;
import commonsos.controller.ad.AdListController;
import commonsos.controller.ad.MyAdsController;
import commonsos.controller.admin.UserSearchController;
import commonsos.controller.auth.AccountCreateController;
import commonsos.controller.auth.LoginController;
import commonsos.controller.auth.LogoutController;
import commonsos.controller.auth.UserController;
import commonsos.controller.community.CommunityListController;
import commonsos.controller.message.*;
import commonsos.controller.transaction.BalanceController;
import commonsos.controller.transaction.TransactionCreateController;
import commonsos.controller.transaction.TransactionListController;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import static java.util.Arrays.asList;
import static spark.Spark.*;

@Slf4j
public class Server {
  @Inject private JsonTransformer toJson;
  @Inject private DatabaseMigrator databaseMigrator;
  @Inject private DemoData demoData;

  private void start() {
    Injector injector = initDependencies();
    databaseMigrator.execute();
    CookieSecuringEmbeddedJettyFactory.register();
    initRoutes(injector);
    demoData.install();
  }

  private Injector initDependencies() {
    Module module = new AbstractModule() {
      @Override protected void configure() {
        bind(Gson.class).toProvider(GsonProvider.class);
        bind(Web3j.class).toInstance(Web3j.build(new HttpService("http://localhost:8545/")));
        bind(ObjectMapper.class).toInstance(new ObjectMapper());
      }
    };

    Injector injector = Guice.createInjector(module, new TransactionInterceptor());
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
    before(new AuthenticationFilter(asList("/login", "/create-account", "/communities")));

    post("/ads", injector.getInstance(AdCreateController.class), toJson);
    get("/ads", injector.getInstance(AdListController.class), toJson);
    get("/ads/:id", injector.getInstance(AdController.class), toJson);
    get("/my-ads", injector.getInstance(MyAdsController.class), toJson);

    get("/balance", injector.getInstance(BalanceController.class), toJson);
    get("/transactions", injector.getInstance(TransactionListController.class), toJson);
    post("/transactions", injector.getInstance(TransactionCreateController.class), toJson);

    post("/message-threads/for-ad/:adId", injector.getInstance(MessageThreadForAdController.class), toJson);
    get("/message-threads/:id", injector.getInstance(MessageThreadController.class), toJson);
    get("/message-threads", injector.getInstance(MessageThreadListController.class), toJson);
    post("/message-threads/:id/messages", injector.getInstance(MessagePostController.class), toJson);
    get("/message-threads/:id/messages", injector.getInstance(MessageListController.class), toJson);

    get("/communities", injector.getInstance(CommunityListController.class), toJson);

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
