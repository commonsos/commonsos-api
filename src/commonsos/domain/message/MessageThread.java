package commonsos.domain.message;

import commonsos.domain.auth.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class MessageThread {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) String id;
  private String adId;
  private String title;
  private String createdBy;
  @ManyToMany
  @JoinTable(
    name = "message_thread_party",
    joinColumns = { @JoinColumn(name = "message_thread_id") },
    inverseJoinColumns = { @JoinColumn(name = "user_id") }
  )
  private List<User> parties = new ArrayList<>();
}
