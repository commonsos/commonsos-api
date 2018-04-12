package commonsos;

import commonsos.domain.agreement.AccountCreateCommand;
import commonsos.domain.auth.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DemoData {

  @Inject UserService userService;

  public void install() {
    userService.create(new AccountCreateCommand().setUsername("worker").setPassword("secret00").setFirstName("Haruto").setLastName("Sato"));
    userService.create(new AccountCreateCommand().setUsername("elderly1").setPassword("secret00").setFirstName("Riku").setLastName("Suzuki"));
    userService.create(new AccountCreateCommand().setUsername("elderly2").setPassword("secret00").setFirstName("Haru").setLastName("Takahashi"));
  }
}
