package commonsos;

import com.google.gson.Gson;
import com.google.inject.*;
import commonsos.controller.ad.AdAcceptController;
import commonsos.controller.ad.AdCreateController;
import commonsos.controller.ad.AdListController;
import commonsos.controller.agreement.AgreementController;
import commonsos.controller.agreement.AgreementListController;

import static spark.Spark.*;

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
    before(new AuthenticationFilter());

    post("/ads", injector.getInstance(AdCreateController.class), toJson);
    post("/ads/:id/accept", injector.getInstance(AdAcceptController.class), toJson);
    get("/ads", injector.getInstance(AdListController.class), toJson);
    get("/agreements", injector.getInstance(AgreementListController.class), toJson);
    get("/agreements/:id", injector.getInstance(AgreementController.class), toJson);

    exception(ForbiddenException.class, (exception, request, response) -> response.status(403));
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
