package commonsos.domain.ad;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class Ad {
  @Id @GeneratedValue(strategy = IDENTITY) private String id;
  private String createdBy;
  private AdType type;
  private String title;
  private String description;
  private BigDecimal points;
  private String location;
  private OffsetDateTime createdAt;
  private String photoUrl;
}
