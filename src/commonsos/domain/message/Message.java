package commonsos.domain.message;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.OffsetDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter @Accessors(chain=true)
public class Message {
  @Id @GeneratedValue(strategy = IDENTITY) private String id;
  private OffsetDateTime createdAt;
  private String createdBy;
  private String threadId;
  private String text;
}
