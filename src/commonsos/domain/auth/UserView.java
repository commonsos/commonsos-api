package commonsos.domain.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class UserView {
  private Long id;
  private String fullName;
  private String description;
  private String location;
  private String avatarUrl;
}