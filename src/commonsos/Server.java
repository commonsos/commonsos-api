package commonsos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.inject.*;
import commonsos.controller.ad.AdAcceptController;
import commonsos.controller.ad.AdCreateController;
import commonsos.controller.ad.AdListController;
import commonsos.controller.ad.AgreementListController;

import java.time.OffsetDateTime;

import static spark.Spark.get;
import static spark.Spark.post;

public class Server {
  @Inject private JsonTransformer toJson;

  private void start() {
    Injector injector = initDependencies();
    initRoutes(injector);
  }

  private Injector initDependencies() {
    Module module = new AbstractModule() {
      @Override protected void configure() {
      }

      @Provides @Singleton Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(OffsetDateTime.class, (JsonSerializer<OffsetDateTime>)(src, typeOfSrc, context) -> new Gson().toJsonTree(src.toString()));
        builder.registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>)(src, typeOfSrc, context) -> OffsetDateTime.parse(src.getAsString()));
        return builder.create();
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
