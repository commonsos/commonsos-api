package commonsos.domain.ad;

import commonsos.DBTest;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserRepository;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static commonsos.TestId.id;
import static commonsos.domain.ad.AdType.GIVE;
import static java.math.BigDecimal.TEN;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

public class AdRepositoryTest extends DBTest {

  private AdRepository repository = new AdRepository(entityManagerService);
  private UserRepository userRepository = new UserRepository(entityManagerService);

  @Test
  public void create() {
    Long id = inTransaction(() -> repository.create(new Ad()).getId());

    assertThat(em().find(Ad.class, id)).isNotNull();
  }

  @Test
  public void findById_notFound() {
    Optional<Ad> result = inTransaction(() -> repository.find(id("unknown")));

    assertFalse(result.isPresent());
  }

  @Test
  public void findById() {
    Long id = inTransaction(() -> repository.create(new Ad()
        .setTitle("Title")
        .setCreatedBy(id("john"))
        .setPoints(TEN).setType(GIVE)
        .setPhotoUrl("url://photo")
        .setCreatedAt(parse("2016-02-02T20:15:30Z"))
        .setDescription("description")
        .setLocation("home"))
      .getId());

    Ad result = repository.find(id).get();

    assertThat(result.getTitle()).isEqualTo("Title");
    assertThat(result.getCreatedBy()).isEqualTo(id("john"));
    assertThat(result.getType()).isEqualTo(GIVE);
    assertThat(result.getPhotoUrl()).isEqualTo("url://photo");
    assertThat(result.getCreatedAt()).isEqualTo(parse("2016-02-02T20:15:30Z"));
    assertThat(result.getDescription()).isEqualTo("description");
    assertThat(result.getLocation()).isEqualTo("home");
  }

  @Test
  public void list() {
    Long id1 = inTransaction(() -> repository.create(new Ad().setCommunityId(id("community"))).getId());
    Long id2 = inTransaction(() -> repository.create(new Ad().setCommunityId(id("other community"))).getId());
    Long id3 = inTransaction(() -> repository.create(new Ad().setCommunityId(id("community"))).getId());

    List<Ad> list = repository.ads(id(("community")));

    assertThat(list).extracting("id").containsExactly(id1, id3);
  }

  @Test
  public void list_filtered_includesOnlyUserCommunity() {
    Long userId1 = inTransaction(() -> userRepository.create(new User()).getId());
    Long userId2 = inTransaction(() -> userRepository.create(new User()).getId());
    Long id1 = inTransaction(() -> repository.create(new Ad().setCreatedBy(userId1).setDescription("text").setCommunityId(id("community"))).getId());
    Long id2 = inTransaction(() -> repository.create(new Ad().setCreatedBy(userId2).setDescription("text").setCommunityId(id("other community"))).getId());

    List<Ad> list = repository.ads(id("community"), "text");

    assertThat(list).extracting("id").containsExactly(id1);
  }

  @Test
  public void list_filtered() {
    Long userId = inTransaction(() -> userRepository.create(new User()).getId());
    Long id1 = inTransaction(() -> repository.create(new Ad().setCreatedBy(userId).setDescription("this does match").setCommunityId(id("community"))).getId());
    Long id2 = inTransaction(() -> repository.create(new Ad().setCreatedBy(userId).setDescription("no match").setCommunityId(id("community"))).getId());
    Long id3 = inTransaction(() -> repository.create(new Ad().setCreatedBy(userId).setDescription("this dOeS match").setCommunityId(id("community"))).getId());
    Long id4 = inTransaction(() -> repository.create(new Ad().setCreatedBy(userId).setTitle("this dOeS match").setCommunityId(id("community"))).getId());

    List<Ad> list = repository.ads(id("community"), "does");

    assertThat(list).extracting("id").containsExactly(id1, id3, id4);
  }

  @Test
  public void list_filteredByAdCreatorName() {
    Long userId1 = inTransaction(() -> userRepository.create(new User().setFirstName("Foo").setLastName("Baz")).getId());
    Long userId2 = inTransaction(() -> userRepository.create(new User().setFirstName("Big").setLastName("Ben")).getId());
    Long userId3 = inTransaction(() -> userRepository.create(new User().setFirstName("Bar").setLastName("Oo")).getId());

    Long id1 = inTransaction(() -> repository.create(new Ad().setCommunityId(id("community")).setCreatedBy(userId1)).getId());
    Long id2 = inTransaction(() -> repository.create(new Ad().setCommunityId(id("community")).setCreatedBy(userId2)).getId());
    Long id3 = inTransaction(() -> repository.create(new Ad().setCommunityId(id("community")).setCreatedBy(userId3)).getId());

    List<Ad> list = repository.ads(id("community"), "oo");

    assertThat(list).extracting("id").containsExactly(id1, id3);
  }

}