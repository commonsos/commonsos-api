package commonsos.domain.ad;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter @Setter @Accessors(chain=true)
public class Ad {
  private String userId;
  private String title;
  private String description;
  private BigDecimal points;
  private String location;
}
