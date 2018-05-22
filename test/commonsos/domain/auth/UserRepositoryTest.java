package commonsos.domain.auth;

import commonsos.DBTest;
import org.junit.Test;

import static commonsos.TestId.id;
import static org.assertj.core.api.Assertions.assertThat;

public class UserRepositoryTest extends DBTest {

  private UserRepository repository = new UserRepository(entityManagerService);

  @Test
  public void create() {
    Long id = inTransaction(() -> repository.create(new User()
      .setUsername("worker")
      .setAdmin(true)
      .setPasswordHash("password hash")
      .setDescription("description")
      .setAvatarUrl("avatar url")
      .setLocation("location")
      .setLastName("last name")
      .setFirstName("first name")
      .setWallet("wallet")
      .setWalletAddress("wallet address")
      .setCommunityId(321L)
    ).getId());

    User created = em().find(User.class, id);
    assertThat(created.getUsername()).isEqualTo("worker");
    assertThat(created.isAdmin()).isTrue();
    assertThat(created.getPasswordHash()).isEqualTo("password hash");
    assertThat(created.getDescription()).isEqualTo("description");
    assertThat(created.getAvatarUrl()).isEqualTo("avatar url");
    assertThat(created.getLocation()).isEqualTo("location");
    assertThat(created.getLastName()).isEqualTo("last name");
    assertThat(created.getFirstName()).isEqualTo("first name");
    assertThat(created.getWallet()).isEqualTo("wallet");
    assertThat(created.getWalletAddress()).isEqualTo("wallet address");
    assertThat(created.getCommunityId()).isEqualTo(321L);
  }

  @Test
  public void findByUsername() {
    inTransaction(() -> repository.create(new User().setUsername("worker")));

    assertThat(repository.findByUsername("worker")).isNotEmpty();
  }

  @Test
  public void update() {
    Long id = inTransaction(() -> repository.create(new User().setUsername("worker").setCommunityId(1L)).getId());
    User user = em().find(User.class, id);
    user.setCommunityId(2L);

    repository.update(user);

    User result = em().find(User.class, id);
    assertThat(result.getCommunityId()).isEqualTo(2L);
  }

  @Test
  public void findByUsername_notFound() {
    assertThat(repository.findByUsername("worker")).isEmpty();
  }

  @Test
  public void findById() {
    Long id = inTransaction(() -> repository.create(new User().setUsername("worker")).getId());

    assertThat(repository.findById(id)).isNotEmpty();
  }

  @Test
  public void findById_notFound() {
    assertThat(repository.findById(id("invalid id"))).isEmpty();
  }

  @Test
  public void search() {
    User user1 = inTransaction(() -> repository.create(new User().setFirstName("first").setLastName("foo").setCommunityId(id("community"))));
    User user2 = inTransaction(() -> repository.create(new User().setFirstName("first").setLastName("bar").setCommunityId(id("community"))));

    assertThat(repository.search(id("community"), "irs")).containsExactly(user1, user2);
    assertThat(repository.search(id("community"), "foo")).containsExactly(user1);
    assertThat(repository.search(id("community"), "baz")).isEmpty();
    assertThat(repository.search(id("community"), " ")).isEmpty();
    assertThat(repository.search(id("community"), "")).isEmpty();
  }

  @Test
  public void search_excludesOtherCommunities() {
    User user1 = inTransaction(() -> repository.create(new User().setFirstName("first").setLastName("foo").setCommunityId(id("Shibuya"))));
    User user2 = inTransaction(() -> repository.create(new User().setFirstName("first").setLastName("bar").setCommunityId(id("Kaga"))));

    assertThat(repository.search(id("Shibuya"), "first")).containsExactly(user1);
    assertThat(repository.search(id("Kaga"), "first")).containsExactly(user2);
  }

  @Test
  public void search_excludesAdminUser() {
    inTransaction(() -> repository.create(new User().setCommunityId(id("community")).setFirstName("name").setLastName("name").setAdmin(true)));
    User user = inTransaction(() -> repository.create(new User().setCommunityId(id("community")).setFirstName("name").setLastName("name").setAdmin(false)));

    assertThat(repository.search(id("community"), "name")).containsExactly(user);
  }

  @Test
  public void findAdminByCommunityId() {
    inTransaction(() -> repository.create(new User().setCommunityId(id("community"))));
    User admin = inTransaction(() -> repository.create(new User().setCommunityId(id("community")).setAdmin(true)));

    User result = repository.findAdminByCommunityId(id("community"));

    assertThat(result).isEqualTo(admin);
  }
}