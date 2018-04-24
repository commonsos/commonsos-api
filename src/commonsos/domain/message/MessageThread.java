package commonsos.domain.message;

import commonsos.domain.auth.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class MessageThread {
  private String id;
  private String adId;
  private String title;
  private String createdBy;
  private List<User> parties = new ArrayList<>();
  private List<Message> messages = new ArrayList<>();
}
