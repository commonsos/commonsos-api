package commonsos.domain.transaction;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Entity @Table(name="transactions")
@Getter @Setter @Accessors(chain=true)
public class Transaction {
  @Id @GeneratedValue(strategy=IDENTITY) private String id;
  private String remitterId;
  private String beneficiaryId;
  private String description;
  private String adId;
  private BigDecimal amount;
  private Instant createdAt;
}
