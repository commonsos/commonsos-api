package commonsos.domain.ad;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class AdRepository {

  List<Ad> ads = new ArrayList<>();

  {
    create(new Ad().setCreatedBy("worker").setTitle("House cleaning").setDescription("Vacuum cleaning, moist cleaning, floors etc").setPoints(new BigDecimal("1299.01")).setLocation("Kaga city"));
    create(new Ad().setCreatedBy("elderly").setTitle("Shopping agent").setDescription("だれか買物に行ってくれないでしょうか？Thank you for reading this article. I had traffic accident last year and chronic pain on left leg\uD83D\uDE22 I want anyone to help me by going shopping to a grocery shop once a week.").setPoints(new BigDecimal("300")).setLocation("Kumasakamachi 熊坂町"));
    create(new Ad().setCreatedBy("elderly").setTitle("小川くん、醤油かってきて").setDescription("刺し身買ってきたから").setPoints(new BigDecimal("1")).setLocation("kaga"));
  }

  public void create(Ad ad) {
    ad.setId(String.valueOf(ads.size()));
    ads.add(ad);
  }

  public List<Ad> list() {
    return ads;
  }

  public Optional<Ad> find(String id) {
    return ads.stream().filter(a -> a.getId().equals(id)).findAny();
  }

  public void save(Ad ad) {
    // changes made in 'stored' objects immediately reflect in repository due to shared object reference
  }
}
