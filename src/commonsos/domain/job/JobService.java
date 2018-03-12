package commonsos.domain.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JobService {
  static public JobService instance = new JobService();

  private List<Job> jobs = new ArrayList<>();

  {
    jobs.add(new Job().setTitle("House cleaning").setDescription("Vacuum cleaning, moist cleaning, floors etc").setPoints(new BigDecimal("1299.01")).setLocation("Kaga city"));
    jobs.add(new Job().setTitle("Shopping agent").setDescription("だれか買物に行ってくれないでしょうか？Thank you for reading this article. I had traffic accident last year and chronic pain on left leg\uD83D\uDE22 I want anyone to help me by going shopping to a grocery shop once a week.").setPoints(new BigDecimal("300")).setLocation("Kumasakamachi 熊坂町"));
    jobs.add(new Job().setTitle("小川くん、醤油かってきて").setDescription("刺し身買ってきたから").setPoints(new BigDecimal("1")).setLocation("kaga"));
  }

  public void create(Job job) {
    jobs.add(job);
  }

  public List<Job> list() {
    return jobs;
  }
}
