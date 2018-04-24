package commonsos.domain.message;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Singleton
public class MessageRepository {

  List<Message> messages = new ArrayList<>();

  public Message create(Message message) {
    message.setId(String.valueOf(messages.size()));
    messages.add(message);
    return message;
  }

  public List<Message> listByThread(String threadId) {
    return messages.stream().filter(m -> m.getThreadId().equals(threadId)).collect(toList());
  }
}
