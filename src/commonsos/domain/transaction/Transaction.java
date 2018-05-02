package commonsos.domain.transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter @Accessors(chain=true)
public class Transaction {
  @Id @GeneratedValue(strategy=IDENTITY) private String id;
  private String remitterId;
  private String beneficiaryId;
  private String description;
  private String adId;
  private BigDecimal amount;
  private OffsetDateTime createdAt;
}
