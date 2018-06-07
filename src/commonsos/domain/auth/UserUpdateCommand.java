package commonsos.domain.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter @Setter @EqualsAndHashCode @Accessors(chain=true) @ToString
public class UserUpdateCommand {
  private String firstName;
  private String lastName;
  private String description;
  private String location;
}