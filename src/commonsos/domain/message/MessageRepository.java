package commonsos.domain.message;

import commonsos.domain.auth.User;

import java.util.ArrayList;
import java.util.Optional;

public class MessageRepository {

  ArrayList<MessageThread> threads = new ArrayList<>();

  public Optional<MessageThread> byAdId(User user, String adId) {
    return threads
      .stream()
      .filter(t -> t.getAdId().equals(adId))
      .filter(t -> t.getCreatedBy().equals(user.getId()))
      .findFirst();
  }

  public MessageThread create(MessageThread messageThread) {
    messageThread.setId(String.valueOf(threads.size()));
    threads.add(messageThread);
    return messageThread;
  }
}
