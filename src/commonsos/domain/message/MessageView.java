package commonsos.domain.message;

import commonsos.domain.auth.UserView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class MessageView {
  private String id;
  private OffsetDateTime createdAt;
  private UserView createdBy;
  private String text;
}
