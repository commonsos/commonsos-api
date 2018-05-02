package commonsos.domain.ad;

import commonsos.DBTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static commonsos.domain.ad.AdType.GIVE;
import static java.math.BigDecimal.TEN;
import static java.time.OffsetDateTime.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

public class AdRepositoryTest extends DBTest {

  private AdRepository repository = new AdRepository();

  @Before
  public void before() {
    repository.emService = entityManagerService;
  }

  @Test
  public void create() {
    String id = inTransaction(() -> repository.create(new Ad()).getId());

    assertThat(repository.em().find(Ad.class, id)).isNotNull();
  }

  @Test
  public void findById_notFound() {
    Optional<Ad> result = inTransaction(() -> repository.find("unknown"));

    assertFalse(result.isPresent());
  }

  @Test
  public void findById() {
    String id = inTransaction(() -> repository.create(new Ad()
        .setTitle("Title")
        .setCreatedBy("john")
        .setPoints(TEN).setType(GIVE)
        .setPhotoUrl("url://photo")
        .setCreatedAt(parse("2016-10-02T20:15:30+03:00"))
        .setDescription("description")
        .setLocation("home"))
      .getId());

    Ad result = repository.find(id).get();

    assertThat(result.getTitle()).isEqualTo("Title");
    assertThat(result.getCreatedBy()).isEqualTo("john");
    assertThat(result.getType()).isEqualTo(GIVE);
    assertThat(result.getPhotoUrl()).isEqualTo("url://photo");
    assertThat(result.getCreatedAt()).isEqualTo(parse("2016-10-02T20:15:30+03:00"));
    assertThat(result.getDescription()).isEqualTo("description");
    assertThat(result.getLocation()).isEqualTo("home");
  }

  @Test
  public void list() {
    String id1 = inTransaction(() -> repository.create(new Ad()).getId());
    String id2 = inTransaction(() -> repository.create(new Ad()).getId());

    List<Ad> list = repository.list();

    assertThat(list).extracting("id").containsExactly(id1, id2);
  }
}