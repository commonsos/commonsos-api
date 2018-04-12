package commonsos.domain.transaction;

import commonsos.domain.auth.UserView;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @Accessors(chain=true)
public class TransactionView {
  private UserView remitter;
  private UserView beneficiary;
  private BigDecimal amount;
  private OffsetDateTime createdAt;
  private boolean debit;
}
