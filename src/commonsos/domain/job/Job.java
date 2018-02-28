package commonsos.domain.job;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter @Setter @Accessors(chain=true)
public class Job {
  private String title;
  private String description;
  private BigDecimal price;
  private String location;
}
