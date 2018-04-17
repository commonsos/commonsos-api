package commonsos.domain.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain=true)
public class AccountCreateCommand {
  private String username;
  private String password;
  private String firstName;
  private String lastName;
  private String location;
}
