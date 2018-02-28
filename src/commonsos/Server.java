package commonsos;

import commonsos.controller.job.JobCreateController;
import commonsos.controller.job.JobListController;
import spark.ResponseTransformer;

import static spark.Spark.get;
import static spark.Spark.post;

public class Server {
  public static void main(String[] args) {
    ResponseTransformer toJson = new JsonTransformer();

    post("/jobs", new JobCreateController(), toJson);
    get("/jobs", new JobListController(), toJson);
  }
}
