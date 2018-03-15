package commonsos.domain.ad;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode
public class Ad {
  private String id;
  private String createdBy;
  private String acceptedBy;
  private String title;
  private String description;
  private BigDecimal points;
  private String location;
}
