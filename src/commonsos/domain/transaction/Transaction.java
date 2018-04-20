package commonsos.domain.transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @Accessors(chain=true)
public class Transaction {
  private String agreementId;
  private String remitterId;
  private String beneficiaryId;
  private String description;
  private String adId;
  private BigDecimal amount;
  private OffsetDateTime createdAt;
}
