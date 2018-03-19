package commonsos;

import com.google.gson.Gson;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import commonsos.controller.ad.AdAcceptController;
import commonsos.controller.ad.AdCreateController;
import commonsos.controller.ad.AdListController;
import commonsos.controller.ad.AgreementListController;

import static spark.Spark.get;
import static spark.Spark.post;

public class Server {
  @Inject private JsonTransformer toJson;

  private void start() {
    Injector injector = initDependencies();
    initRoutes(injector);
  }

  private Injector initDependencies() {
    AbstractModule module = new AbstractModule() {
      @Override protected void configure() {
        bind(Gson.class).toInstance(new Gson());
      }
    };

    Injector injector = Guice.createInjector(module);
    injector.injectMembers(this);
    return injector;
  }

  private void initRoutes(Injector injector) {
    post("/ads", injector.getInstance(AdCreateController.class), toJson);
    post("/ads/:id/accept", injector.getInstance(AdAcceptController.class), toJson);
    get("/ads", injector.getInstance(AdListController.class), toJson);
    get("/agreements", injector.getInstance(AgreementListController.class), toJson);
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
