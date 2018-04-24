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
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Singleton
public class MessageService {

  @Inject private MessageRepository repository;
  @Inject private AdService adService;
  @Inject private UserService userService;

  public MessageThreadView threadForAd(User user, String adId) {
    MessageThread thread = repository.byAdId(user, adId).orElseGet(() -> createMessageThreadForAd(user, adId));
    return view(user, thread);
  }

  public MessageThreadView thread(User user, String threadId) {
    return repository.thread(threadId)
      .map(t -> checkAccess(user, t))
      .map(t -> view(user, t))
      .orElseThrow(BadRequestException::new);
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

    return repository.create(messageThread);
  }

  public MessageThreadView view(User user, MessageThread thread) {
    List<UserView> users = thread.getUsers().stream()
      .filter(u -> !u.equals(user))
      .map(userService::view)
      .collect(toList());

    return new MessageThreadView()
      .setId(thread.getId())
      .setTitle(thread.getTitle())
      .setMessages(Collections.emptyList())
      .setUsers(users);
  }

  public List<MessageThreadView> threads(User user) {
    return repository.listByUser(user).stream().map(thread -> view(user, thread)).collect(toList());
  }
}
