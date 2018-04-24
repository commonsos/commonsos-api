package commonsos.domain.message;

import commonsos.BadRequestException;
import commonsos.ForbiddenException;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Singleton
public class MessageService {

  @Inject private MessageThreadRepository messageThreadRepository;
  @Inject private MessageRepository messageRepository;
  @Inject private AdService adService;
  @Inject private UserService userService;

  public MessageThreadView threadForAd(User user, String adId) {
    MessageThread thread = messageThreadRepository.byAdId(user, adId).orElseGet(() -> createMessageThreadForAd(user, adId));
    return view(user, thread);
  }

  public MessageThreadView thread(User user, String threadId) {
    return messageThreadRepository.thread(threadId)
      .map(t -> checkAccess(user, t))
      .map(this::loadMessages)
      .map(t -> view(user, t))
      .orElseThrow(BadRequestException::new);
  }

  MessageThread loadMessages(MessageThread thread) {
    thread.setMessages(messageRepository.listByThread(thread.getId()));
    return thread;
  }

  private MessageThread checkAccess(User user, MessageThread thread) {
    if (!thread.getUsers().contains(user)) throw new ForbiddenException();
    return thread;
  }

  MessageThread createMessageThreadForAd(User user, String adId) {
    Ad ad = adService.ad(adId);
    User adCreator = userService.user(ad.getCreatedBy());

    MessageThread messageThread = new MessageThread()
      .setCreatedBy(user.getId())
      .setTitle(ad.getTitle()).setAdId(adId)
      .setUsers(asList(adCreator, user));

    return messageThreadRepository.create(messageThread);
  }

  public MessageThreadView view(User user, MessageThread thread) {
    List<UserView> users = thread.getUsers().stream()
      .filter(u -> !u.equals(user))
      .map(userService::view)
      .collect(toList());

    List<MessageView> messages = thread.getMessages().stream().map(this::view).collect(toList());

    return new MessageThreadView()
      .setId(thread.getId())
      .setTitle(thread.getTitle())
      .setMessages(messages)
      .setUsers(users);
  }

  MessageView view(Message message) {
    return new MessageView()
      .setId(message.getId())
      .setCreatedAt(message.getCreatedAt())
      .setCreatedBy(userService.view(message.getCreatedBy()))
      .setText(message.getText());
  }

  public List<MessageThreadView> threads(User user) {
    return messageThreadRepository
      .listByUser(user)
      .stream()
      .map(this::loadMessages)
      .map(thread -> view(user, thread))
      .collect(toList());
  }

  public void postMessage(User user, MessagePostCommand command) {
    messageThreadRepository.thread(command.getThreadId()).map(thread -> checkAccess(user, thread));
    messageRepository.create(new Message()
      .setCreatedBy(user.getId())
      .setCreatedAt(now())
      .setThreadId(command.getThreadId())
      .setText(command.getText()));
  }
}
