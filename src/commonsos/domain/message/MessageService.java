package commonsos.domain.message;

import commonsos.BadRequestException;
import commonsos.ForbiddenException;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import commonsos.domain.ad.AdView;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static java.time.Instant.now;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

@Singleton
@Slf4j
public class MessageService {

  @Inject private MessageThreadRepository messageThreadRepository;
  @Inject private MessageRepository messageRepository;
  @Inject private AdService adService;
  @Inject private UserService userService;

  public MessageThreadView threadForAd(User user, Long adId) {
    MessageThread thread = messageThreadRepository.byAdId(user, adId).orElseGet(() -> createMessageThreadForAd(user, adId));
    return view(user, thread);
  }

  public MessageThreadView threadWithUser(User user, Long otherUserId) {
    MessageThread thread = messageThreadRepository.betweenUsers(user.getId(), otherUserId)
      .orElseGet(() -> createMessageThreadWithUser(user, otherUserId));
    return view(user, thread);
  }

  MessageThread createMessageThreadWithUser(User user, Long otherUserId) {
    User otherUser = userService.user(otherUserId);
    MessageThread messageThread = new MessageThread()
      .setCreatedBy(user.getId())
      .setCreatedAt(now())
      .setParties(asList(new MessageThreadParty().setUser(user), new MessageThreadParty().setUser(otherUser)));

    return messageThreadRepository.create(messageThread);
  }

  public MessageThreadView group(User user, CreateGroupCommand command) {
    List<User> users = validatePartiesCommunity(user.getCommunityId(), command.getMemberIds());
    List<MessageThreadParty> parties = usersToParties(users);
    parties.add(new MessageThreadParty().setUser(user));

    MessageThread messageThread = new MessageThread()
      .setGroup(true)
      .setTitle(command.title)
      .setCreatedBy(user.getId())
      .setCreatedAt(now())
      .setParties(parties);
    return view(user, messageThreadRepository.create(messageThread));
  }

  private List<MessageThreadParty> usersToParties(List<User> users) {
    return users.stream().map(u -> new MessageThreadParty().setUser(u)).collect(toList());
  }

  public MessageThreadView updateGroup(User user, GroupMessageThreadUpdateCommand command) {
    MessageThread messageThread = messageThreadRepository.thread(command.threadId).orElseThrow(ForbiddenException::new);
    if (!messageThread.isGroup()) throw new BadRequestException("Not a group message thread");
    if (!isUserAllowedToAccessMessageThread(user, messageThread)) throw new ForbiddenException("Not a thread member");

    List<User> existingUsers = messageThread.getParties().stream().map(MessageThreadParty::getUser).collect(toList());
    List<User> givenUsers = validatePartiesCommunity(user.getCommunityId(), command.getMemberIds());
    List<User> newUsers = givenUsers.stream()
      .filter(u -> !existingUsers.stream().anyMatch(eu -> eu.getId().equals(u.getId())))
      .collect(toList());

    List<MessageThreadParty> newParties = usersToParties(newUsers);
    messageThread.getParties().addAll(newParties);
    messageThread.setTitle(command.getTitle());
    messageThreadRepository.update(messageThread);

    return view(user, messageThread);
  }

  List<User> validatePartiesCommunity(Long communityId, List<Long> memberIds) {
    List<User> users = memberIds.stream().map(id -> userService.user(id)).collect(toList());
    if (users.isEmpty()) throw new BadRequestException("No group members specified");
    users.forEach(user1 -> {
      if (!communityId.equals(user1.getCommunityId())) {
        String message = String.format("Tried to create group chat with user %s from different community", user1.getUsername());
        throw new ForbiddenException(message);
      }
    });
    return users;
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
      .setCreatedAt(now())
      .setTitle(ad.getTitle()).setAdId(adId)
      .setParties(asList(new MessageThreadParty().setUser(adCreator), new MessageThreadParty().setUser(user)));

    return messageThreadRepository.create(messageThread);
  }

  public MessageThreadView view(User user, MessageThread thread) {
    List<UserView> parties = thread.getParties().stream()
      .filter(p -> !p.getUser().getId().equals(thread.getCreatedBy()))
      .map(MessageThreadParty::getUser)
      .map(userService::view)
      .collect(toList());

    UserView creator = thread.getParties().stream()
      .filter(p -> p.getUser().getId().equals(thread.getCreatedBy()))
      .map(MessageThreadParty::getUser)
      .map(userService::view).findFirst().orElseThrow(RuntimeException::new);

    UserView counterParty = concat(parties.stream(), of(creator))
      .filter(uv -> uv.getId() != user.getId())
      .findFirst()
      .orElseThrow(RuntimeException::new);

    AdView ad = thread.getAdId() == null ? null : adService.view(user, thread.getAdId());
    MessageView lastMessage = messageRepository.lastMessage(thread.getId()).map(this::view).orElse(null);

    return new MessageThreadView()
      .setId(thread.getId())
      .setAd(ad)
      .setTitle(thread.getTitle())
      .setLastMessage(lastMessage)
      .setCreatedAt(thread.getCreatedAt())
      .setGroup(thread.isGroup())
      .setCreator(creator)
      .setCounterParty(counterParty)
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
    List<Long> unreadMessageThreadIds = messageThreadRepository.unreadMessageThreadIds(user);
    List<MessageThreadView> threadViews = messageThreadRepository
      .listByUser(user)
      .stream()
      .map(thread -> view(user, thread))
      .filter(t -> t.getLastMessage() != null || t.isGroup())
      .map(p -> p.setUnread(unreadMessageThreadIds.contains(p.getId())))
      .collect(toList());
    return sortAsNewestFirst(threadViews);
  }

  List<MessageThreadView> sortAsNewestFirst(List<MessageThreadView> threadViews) {
    threadViews.sort(comparing((MessageThreadView t) -> {
      if (t.getLastMessage() == null) return t.getCreatedAt();
      return t.getLastMessage().getCreatedAt();
    }).reversed());
    return threadViews;
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
    return messageThreadRepository.unreadMessageThreadIds(user).size();
  }
}
