package commonsos.domain.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Instant;

@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class MessageView {
  private Long id;
  private Instant createdAt;
  private Long createdBy;
  private String text;
}
