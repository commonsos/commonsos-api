package commonsos.domain.message;

import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class MessageService {

  @Inject private MessageRepository repository;
  @Inject private AdService adService;
  @Inject private UserService userService;

  public MessageThreadView thread(User user, String adId) {
    return view(
      repository.byAdId(user, adId).orElseGet(() -> createMessageThreadForAd(user, adId))
    );
  }

  MessageThread createMessageThreadForAd(User user, String adId) {
    Ad ad = adService.ad(adId);
    User adCreator = userService.user(ad.getCreatedBy());

    MessageThread messageThread = new MessageThread()
      .setCreatedBy(user.getId())
      .setTitle(ad.getTitle()).setAdId(adId)
      .setUsers(asList(adCreator));

    return repository.create(messageThread);
  }

  public MessageThreadView view(MessageThread thread) {
    List<UserView> users = thread.getUsers().stream().map(userService::view).collect(toList());
    return new MessageThreadView()
      .setId(thread.getId())
      .setTitle(thread.getTitle())
      .setMessages(Collections.emptyList())
      .setUsers(users);
  }
}
