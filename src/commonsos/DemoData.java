package commonsos;

import commonsos.domain.agreement.AccountCreateCommand;
import commonsos.domain.auth.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DemoData {

  @Inject UserService userService;

  public void install() {
    userService.create(new AccountCreateCommand().setUsername("worker").setPassword("secret00").setFirstName("Haruto").setLastName("Sato").setLocation("Shibuya, Tokyo, Japan"));
    userService.create(new AccountCreateCommand().setUsername("elderly1").setPassword("secret00").setFirstName("Riku").setLastName("Suzuki").setLocation("Kaga, Ishikawa Prefecture, Japan"));
    userService.create(new AccountCreateCommand().setUsername("elderly2").setPassword("secret00").setFirstName("Haru").setLastName("Takahashi").setLocation("Kaga, Ishikawa Prefecture, Japan"));
    userService.create(new AccountCreateCommand().setUsername("admin").setPassword("secret00").setFirstName("Kaito").setLastName("Kobayashi").setLocation("Kaga, Ishikawa Prefecture, Japan"))
      .setAdmin(true);
  }
}
