package commonsos.domain.message;

import commonsos.domain.auth.UserView;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class MessageThreadView {
  private String id;
  private String adId;
  private String title;
  private List<UserView> parties;
  private List<MessageView> messages;
  private MessageView lastMessage;
}
