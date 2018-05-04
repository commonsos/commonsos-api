package commonsos.domain.ad;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Entity @Table(name="ads")
@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class Ad {
  @Id @GeneratedValue(strategy = IDENTITY) private Long id;
  private Long createdBy;
  @Enumerated(value = STRING) private AdType type;
  private String title;
  private String description;
  private BigDecimal points;
  private String location;
  private Instant createdAt;
  private String photoUrl;
}
