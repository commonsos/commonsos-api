package commonsos;

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
import commonsos.controller.message.*;
import commonsos.controller.transaction.BalanceController;
import commonsos.controller.transaction.TransactionCreateController;
import commonsos.controller.transaction.TransactionListController;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

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
    before(new AuthenticationFilter(asList("/login", "/create-account")));

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

  private static void startH2WebConsole() {
    try {
      org.h2.tools.Server server = org.h2.tools.Server.createWebServer(null);
      server.start();
      log.info("H2 Web server started on port " + server.getPort());
    }
    catch (SQLException e) {
      log.warn("Failed to start H2 Web server", e);
    }
  }


  public static void main(String[] args) {
    try {
      new Server().start();
      if (args.length > 0 && "h2-console".equals(args[0])) Server.startH2WebConsole();
    }
    catch (Throwable e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
