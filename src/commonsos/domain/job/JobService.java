package commonsos.domain.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JobService {
  static public JobService instance = new JobService();

  private List<Job> jobs = new ArrayList<>();

  { jobs.add(new Job().setTitle("House cleaning").setDescription("Vacuum cleaning, moist cleaning, floors etc").setPrice(new BigDecimal("1299.01")).setLocation("Kaga city")); }

  public void create(Job job) {
    jobs.add(job);
  }

  public List<Job> list() {
    return jobs;
  }
}
