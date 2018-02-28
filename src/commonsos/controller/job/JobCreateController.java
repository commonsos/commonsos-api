package commonsos.controller.job;

import com.google.gson.Gson;
import commonsos.domain.job.Job;
import commonsos.domain.job.JobService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JobCreateController implements Route {

  private JobService service = JobService.instance;
  private Gson gson = new Gson();

  @Override public Object handle(Request request, Response response) throws Exception {
    Job job = gson.fromJson(request.body(), Job.class);
    service.create(job);
    return "";
  }
}
