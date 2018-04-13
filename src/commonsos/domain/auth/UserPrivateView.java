package commonsos.domain.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter @Setter @Accessors(chain=true)
public class UserPrivateView {
  private String id;
  private String fullName;
  private BigDecimal balance;
  private String location;
}