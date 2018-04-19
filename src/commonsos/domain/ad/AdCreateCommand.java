package commonsos.domain.ad;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter @Setter @Accessors(chain=true)
public class AdCreateCommand {
  private String title;
  private String description;
  private BigDecimal amount;
  private String location;
}
