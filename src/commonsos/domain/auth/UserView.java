package commonsos.domain.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter @Setter @Accessors(chain=true)
public class UserView {
  private String id;
  private String username;
  private String fullName;
  private BigDecimal balance;
}
