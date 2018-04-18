package commonsos.domain.ad;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.OffsetDateTime.now;

@Singleton
public class AdRepository {

  List<Ad> ads = new ArrayList<>();

  {
    create(new Ad()
      .setCreatedBy("0")
      .setTitle("House cleaning")
      .setDescription("Vacuum cleaning, moist cleaning, floors etc")
      .setPoints(new BigDecimal("1299.01"))
      .setLocation("Kaga city")
      .setCreatedAt(now())
      .setPhotoUrl("/static/temp/sample-photo-apartment1.jpg")
    );
    create(new Ad()
      .setCreatedBy("1")
      .setTitle("Shopping agent")
      .setDescription("Thank you for reading this article. I had traffic accident last year and chronic pain on left leg\uD83D\uDE22 I want anyone to help me by going shopping to a grocery shop once a week.")
      .setPoints(new BigDecimal("300"))
      .setLocation("Kumasakamachi 熊坂町")
      .setCreatedAt(now())
      .setPhotoUrl("/static/temp/shop.jpeg")
    );
    create(new Ad()
      .setCreatedBy("2")
      .setTitle("小川くん、醤油かってきて")
      .setDescription("刺し身買ってきたから")
      .setPoints(new BigDecimal("1"))
      .setLocation("kaga")
      .setCreatedAt(now())
      .setPhotoUrl("/static/temp/soy.jpeg")
    );
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
