package commonsos.domain.ad;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @Accessors(chain=true)
public class AdView {
  private String id;
  private String createdBy;
  private String title;
  private String description;
  private BigDecimal points;
  private String location;
  private boolean acceptable;
  private OffsetDateTime createdAt;
  private String photoUrl;
}
