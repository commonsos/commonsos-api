package commonsos.domain.message;

import commonsos.BadRequestException;
import commonsos.ForbiddenException;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import commonsos.domain.ad.AdView;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static java.time.Instant.now;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Singleton
public class MessageService {

  @Inject private MessageThreadRepository messageThreadRepository;
  @Inject private MessageRepository messageRepository;
  @Inject private AdService adService;
  @Inject private UserService userService;

  public MessageThreadView threadForAd(User user, Long adId) {
    MessageThread thread = messageThreadRepository.byAdId(user, adId).orElseGet(() -> createMessageThreadForAd(user, adId));
    return view(user, thread);
  }

  public MessageThreadView thread(User user, Long threadId) {
    return messageThreadRepository.thread(threadId)
      .map(t -> checkAccess(user, t))
      .map(t -> view(user, t))
      .orElseThrow(BadRequestException::new);
  }

  private MessageThread checkAccess(User user, MessageThread thread) {
    if (!isUserAllowedToAccessMessageThread(user, thread)) throw new ForbiddenException();
    return thread;
  }

  MessageThread createMessageThreadForAd(User user, Long adId) {
    Ad ad = adService.ad(adId);
    User adCreator = userService.user(ad.getCreatedBy());

    MessageThread messageThread = new MessageThread()
      .setCreatedBy(user.getId())
      .setTitle(ad.getTitle()).setAdId(adId)
      .setParties(asList(new MessageThreadParty().setUser(adCreator), new MessageThreadParty().setUser(user)));

    return messageThreadRepository.create(messageThread);
  }

  public MessageThreadView view(User user, MessageThread thread) {
    List<UserView> parties = thread.getParties().stream()
      .filter(p -> !p.getUser().equals(user))
      .map(MessageThreadParty::getUser)
      .map(userService::view)
      .collect(toList());

    AdView ad = thread.getAdId() == null ? null : adService.view(user, thread.getAdId());
    MessageView lastMessage = messageRepository.lastMessage(thread.getId()).map(this::view).orElse(null);

    return new MessageThreadView()
      .setId(thread.getId())
      .setAd(ad)
      .setTitle(thread.getTitle())
      .setLastMessage(lastMessage)
      .setParties(parties);
  }

  MessageView view(Message message) {
    return new MessageView()
      .setId(message.getId())
      .setCreatedAt(message.getCreatedAt())
      .setCreatedBy(message.getCreatedBy())
      .setText(message.getText());
  }

  public List<MessageThreadView> threads(User user) {
    List<MessageThreadView> threadViews = messageThreadRepository
      .listByUser(user)
      .stream()
      .map(thread -> view(user, thread))
      .filter(t -> t.getLastMessage() != null)
      .collect(toList());
    sortThreadsByLastMessageTime(threadViews);
    return threadViews;
  }

  void sortThreadsByLastMessageTime(List<MessageThreadView> threadViews) {
    threadViews.sort(comparing((MessageThreadView t) -> t.getLastMessage().getCreatedAt()).reversed());
  }

  public MessageView postMessage(User user, MessagePostCommand command) {
    messageThreadRepository.thread(command.getThreadId()).map(thread -> checkAccess(user, thread));
    Message message = messageRepository.create(new Message()
      .setCreatedBy(user.getId())
      .setCreatedAt(now())
      .setThreadId(command.getThreadId())
      .setText(command.getText()));
    return view(message);
  }

  public List<MessageView> messages(User user, Long threadId) {
    MessageThread thread = messageThreadRepository.thread(threadId).orElseThrow(BadRequestException::new);
    if (!isUserAllowedToAccessMessageThread(user, thread)) throw new ForbiddenException();

    markVisited(user, thread);

    return messageRepository.listByThread(threadId).stream().map(this::view).collect(toList());
  }

  private void markVisited(User user, MessageThread thread) {
    MessageThreadParty me = thread.getParties().stream().filter(p -> p.getUser().equals(user)).findFirst().orElseThrow(RuntimeException::new);
    me.setVisitedAt(now());
    messageThreadRepository.update(me);
  }

  private boolean isUserAllowedToAccessMessageThread(User user, MessageThread thread) {
    return thread.getParties().stream().anyMatch(p -> p.getUser().equals(user));
  }

  public int unreadMessageThreadCount(User user) {
    return messageThreadRepository.unreadMessageThreadCount(user);
  }
}
