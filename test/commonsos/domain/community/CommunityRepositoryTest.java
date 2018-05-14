package commonsos.domain.community;

import commonsos.DBTest;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CommunityRepositoryTest extends DBTest {

  CommunityRepository repository = new CommunityRepository(entityManagerService);

  @Test
  public void findById() {
    Long id = inTransaction(() -> {
      Community cats = new Community().setName("Kaga").setTokenContractId("66");
      em().persist(cats);
      return cats.getId();
    });

    Optional<Community> community = repository.findById(id);

    assertThat(community).isNotEmpty();
    assertThat(community.get().getId()).isEqualTo(id);
    assertThat(community.get().getName()).isEqualTo("Kaga");
    assertThat(community.get().getTokenContractId()).isEqualTo("66");
  }

  @Test
  public void findById_notFound() {
    assertThat(repository.findById(123L)).isEmpty();
  }

  @Test
  public void create() {
    Long id = inTransaction(() -> repository.create(new Community().setName("Kaga community").setTokenContractId("0x1234567")).getId());

    Community community = em().find(Community.class, id);

    assertThat(community.getName()).isEqualTo("Kaga community");
    assertThat(community.getTokenContractId()).isEqualTo("0x1234567");
  }

  @Test
  public void list() {
    inTransaction(() -> {
      em().persist(new Community().setName("Kaga").setTokenContractId("66"));
      em().persist(new Community().setName("Tokio").setTokenContractId(null));
    });

    List<Community> result = repository.list();

    assertThat(result).extracting("name").containsExactly("Kaga");
  }
}