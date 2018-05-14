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
    User user1 = inTransaction(() -> repository.create(new User().setFirstName("first").setLastName("foo")));
    User user2 = inTransaction(() -> repository.create(new User().setFirstName("first").setLastName("bar")));

    assertThat(repository.search("irs")).containsExactly(user1, user2);
    assertThat(repository.search("foo")).containsExactly(user1);
    assertThat(repository.search("baz")).isEmpty();
    assertThat(repository.search(" ")).isEmpty();
    assertThat(repository.search("")).isEmpty();
  }

  @Test
  public void search_excludesAdminUser() {
    inTransaction(() -> repository.create(new User().setFirstName("name").setAdmin(true)));
    User user = inTransaction(() -> repository.create(new User().setFirstName("name").setAdmin(false)));

    assertThat(repository.search("name")).containsExactly(user);
  }
}