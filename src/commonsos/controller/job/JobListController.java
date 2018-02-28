package commonsos.controller.job;

import commonsos.domain.job.JobService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JobListController implements Route {
  private JobService service = JobService.instance;

  @Override public Object handle(Request request, Response response) throws Exception {
    return service.list();
  }
}
