package commonsos.domain.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class UserView {
  private String id;
  private String fullName;
  private String location;
  private String avatarUrl;
}