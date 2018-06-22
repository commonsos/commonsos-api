package commonsos.domain.message;

import commonsos.domain.ad.AdView;
import commonsos.domain.auth.UserView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.List;

@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class MessageThreadView {
  private Long id;
  private AdView ad;
  private String title;
  private List<UserView> parties;
  private UserView creator;
  private UserView counterParty;
  private MessageView lastMessage;
  private boolean unread;
  private boolean group;
  private Instant createdAt;
}
