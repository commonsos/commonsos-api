package commonsos;

import commonsos.controller.ad.AdCreateController;
import commonsos.controller.ad.AdListController;
import spark.ResponseTransformer;

import static spark.Spark.get;
import static spark.Spark.post;

public class Server {
  public static void main(String[] args) {
    ResponseTransformer toJson = new JsonTransformer();

    post("/ads", new AdCreateController(), toJson);
    get("/ads", new AdListController(), toJson);
  }
}
