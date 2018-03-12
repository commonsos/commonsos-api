package commonsos.domain.ad;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AdService {
  private List<Ad> ads = new ArrayList<>();

  {
    ads.add(new Ad().setTitle("House cleaning").setDescription("Vacuum cleaning, moist cleaning, floors etc").setPoints(new BigDecimal("1299.01")).setLocation("Kaga city"));
    ads.add(new Ad().setTitle("Shopping agent").setDescription("だれか買物に行ってくれないでしょうか？Thank you for reading this article. I had traffic accident last year and chronic pain on left leg\uD83D\uDE22 I want anyone to help me by going shopping to a grocery shop once a week.").setPoints(new BigDecimal("300")).setLocation("Kumasakamachi 熊坂町"));
    ads.add(new Ad().setTitle("小川くん、醤油かってきて").setDescription("刺し身買ってきたから").setPoints(new BigDecimal("1")).setLocation("kaga"));
  }

  public void create(Ad ad) {
    ads.add(ad);
  }

  public List<Ad> list() {
    return ads;
  }
}
