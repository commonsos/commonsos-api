package commonsos.domain.ad;

import commonsos.domain.auth.UserView;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @Accessors(chain=true)
public class AdView {
  private String id;
  private UserView createdBy;
  private String title;
  private String description;
  private BigDecimal points;
  private String location;
  private boolean own;
  private boolean payable;
  private OffsetDateTime createdAt;
  private String photoUrl;
  private AdType type;
}
