package commonsos.domain.message;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;
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
    return messages.stream()
      .filter(m -> m.getThreadId().equals(threadId))
      .sorted(comparing(Message::getCreatedAt))
      .collect(toList());
  }

  public Optional<Message> lastMessage(String threadId) {
    List<Message> messages = listByThread(threadId);
    return messages.size() > 0 ? Optional.of(messages.get(messages.size()-1)) : Optional.empty();
  }
}
