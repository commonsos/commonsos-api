package commonsos.domain.message;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MessageRepository {

  List<Message> messages = new ArrayList<>();

  public Message create(Message message) {
    message.setId(String.valueOf(messages.size()));
    messages.add(message);
    return message;
  }
}
