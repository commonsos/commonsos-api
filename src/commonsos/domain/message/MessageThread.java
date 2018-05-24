package commonsos.domain.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="message_threads")
@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class MessageThread {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
  private Long adId;
  private String title;
  private Long createdBy;

  @OneToMany(cascade = CascadeType.PERSIST)
  @JoinColumn(name = "message_thread_id", referencedColumnName = "id")
  private List<MessageThreadParty> parties = new ArrayList<>();
}
