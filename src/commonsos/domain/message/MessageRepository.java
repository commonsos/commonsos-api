package commonsos.domain.message;

import commonsos.domain.auth.User;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Singleton
public class MessageRepository {

  ArrayList<MessageThread> threads = new ArrayList<>();

  public Optional<MessageThread> byAdId(User user, String adId) {
    return threads
      .stream()
      .filter(t -> t.getAdId().equals(adId))
      .filter(t -> t.getCreatedBy().equals(user.getId()))
      .findAny();
  }

  public MessageThread create(MessageThread messageThread) {
    messageThread.setId(String.valueOf(threads.size()));
    threads.add(messageThread);
    return messageThread;
  }

  public List<MessageThread> listByUser(User user) {
    return threads.stream().filter(t -> t.getUsers().contains(user)).collect(toList());
  }

  public Optional<MessageThread> thread(String id) {
    return threads.stream().filter(t -> t.getId().equals(id)).findAny();
  }
}
