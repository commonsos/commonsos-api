package commonsos.domain.transaction;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class TransactionCreateCommand {
  private Long beneficiaryId;
  private String description;
  private BigDecimal amount;
  private Long adId;
}
