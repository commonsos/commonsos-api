package commonsos.domain.message;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Getter @Setter @Accessors(chain=true)
public class Message {
  private String id;
  private OffsetDateTime createdAt;
  private String createdBy;
  private String threadId;
  private String text;
}
