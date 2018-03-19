package commonsos.domain.agreement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @Accessors(chain = true)
public class Agreement {
  private String adId;
  private String providerId;
  private String consumerId;
  private String title;
  private String description;
  private BigDecimal points;
  private String location;
  private OffsetDateTime createdAt;
}
