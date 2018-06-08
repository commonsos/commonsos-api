package commonsos.domain.message;

import commonsos.domain.auth.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.Instant;

import static javax.persistence.GenerationType.IDENTITY;

@Entity @Table(name = "message_thread_parties")
@Getter @Setter @Accessors(chain = true) @EqualsAndHashCode @ToString
public class MessageThreadParty {
  @Id @GeneratedValue(strategy = IDENTITY) private Long id;
  @Column(name = "message_thread_id") private Long messageThreadId;
  @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
  private Instant visitedAt;
}