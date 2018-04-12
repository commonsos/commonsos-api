package commonsos;

import commonsos.domain.agreement.AccountCreateCommand;
import commonsos.domain.auth.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DemoData {

  @Inject UserService userService;

  public void install() {
    userService.create(new AccountCreateCommand().setUsername("worker").setPassword("secret").setFirstName("Haruto").setLastName("Sato"));
    userService.create(new AccountCreateCommand().setUsername("elderly1").setPassword("secret1").setFirstName("Riku").setLastName("Suzuki"));
    userService.create(new AccountCreateCommand().setUsername("elderly2").setPassword("secret2").setFirstName("Haru").setLastName("Takahashi"));
  }
}
