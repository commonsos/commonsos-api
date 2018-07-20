package commonsos.domain.message;

import com.google.common.collect.ImmutableMap;
import commonsos.BadRequestException;
import commonsos.ForbiddenException;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import commonsos.domain.ad.AdView;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static commonsos.TestId.id;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

public class MessageServiceTest {

  @Mock MessageThreadRepository messageThreadRepository;
  @Mock MessageRepository messageRepository;
  @Mock AdService adService;
  @Mock UserService userService;
  @Mock PushNotificationService pushNotificationService;
  @InjectMocks @Spy MessageService service;
  @Captor ArgumentCaptor<MessageThread> messageThreadArgumentCaptor;
  @Captor ArgumentCaptor<MessageThread> messageThreadArgumentCaptor2;

  @Test
  public void threadForAd_findExisting() {
    MessageThread messageThread = new MessageThread();
    User user = new User();
    when(messageThreadRepository.byAdId(user, id("ad-id"))).thenReturn(Optional.of(messageThread));
    MessageThreadView messageThreadView = new MessageThreadView();
    doReturn(messageThreadView).when(service).view(user, messageThread);

    MessageThreadView result = service.threadForAd(user, id("ad-id"));

    assertThat(result).isSameAs(messageThreadView);
  }

  @Test
  public void threadForAd_createNewIfNotPresent() {
    User user = new User().setId(id("user id"));
    when(messageThreadRepository.byAdId(user, id("ad-id"))).thenReturn(empty());

    MessageThread newThread = new MessageThread();
    doReturn(newThread).when(service).createMessageThreadForAd(user, id("ad-id"));

    MessageThreadView messageThreadView = new MessageThreadView();
    doReturn(messageThreadView).when(service).view(user, newThread);


    MessageThreadView result = service.threadForAd(user, id("ad-id"));


    assertThat(result).isEqualTo(messageThreadView);
  }

  @Test
  public void threadWithUser_existingThread() {
    User user = new User().setId(id("my id"));
    MessageThread existingThread = new MessageThread();
    when(messageThreadRepository.betweenUsers(id("my id"), id("other user id"))).thenReturn(Optional.of(existingThread));
    MessageThreadView messageThreadView = new MessageThreadView();
    doReturn(messageThreadView).when(service).view(user, existingThread);

    MessageThreadView result = service.threadWithUser(user, id("other user id"));

    assertThat(result).isSameAs(messageThreadView);
  }

  @Test
  public void threadWithUser_createNew() {
    User user = new User().setId(id("my id"));
    when(messageThreadRepository.betweenUsers(id("my id"), id("other user id"))).thenReturn(empty());
    MessageThread createdThread = new MessageThread();
    doReturn(createdThread).when(service).createMessageThreadWithUser(user, id("other user id"));
    MessageThreadView messageThreadView = new MessageThreadView();
    doReturn(messageThreadView).when(service).view(user, createdThread);

    MessageThreadView result = service.threadWithUser(user, id("other user id"));

    assertThat(result).isSameAs(messageThreadView);
  }

  @Test
  public void createMessageThreadWithUser() {
    User user = new User().setId(id("user id"));
    User counterparty = new User().setId(id("counterparty id"));
    MessageThread newThread = new MessageThread();
    when(messageThreadRepository.create(messageThreadArgumentCaptor.capture())).thenReturn(newThread);
    when(userService.user(id("counterparty id"))).thenReturn(counterparty);

    MessageThread result = service.createMessageThreadWithUser(user, id("counterparty id"));

    assertThat(result).isEqualTo(newThread);
    MessageThread createdThread = messageThreadArgumentCaptor.getValue();
    assertThat(createdThread.getCreatedBy()).isEqualTo(id("user id"));
    assertThat(createdThread.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
    assertThat(createdThread.isGroup()).isFalse();
    assertThat(createdThread.getParties()).extracting("user").containsExactly(user, counterparty);
  }

  @Test
  public void thread() {
    User user = new User().setId(id("user id"));
    MessageThread messageThread = new MessageThread().setParties(asList(party(user)));
    when(messageThreadRepository.thread(id("thread-id"))).thenReturn(Optional.of(messageThread));
    MessageThreadView messageThreadView = new MessageThreadView();
    doReturn(messageThreadView).when(service).view(user, messageThread);

    assertThat(service.thread(user, id("thread-id"))).isSameAs(messageThreadView);
  }

  private MessageThreadParty party(User user) {
    return new MessageThreadParty().setUser(user);
  }

  @Test(expected = ForbiddenException.class)
  public void thread_canOnlyAccessThreadParticipatingIn() {
    MessageThread messageThread = new MessageThread().setParties(asList(party(new User().setId(id("other user")))));
    when(messageThreadRepository.thread(id("thread-id"))).thenReturn(Optional.of(messageThread));

    service.thread(new User().setId(id("user id")), id("thread-id"));
  }

  @Test(expected = BadRequestException.class)
  public void thread_notFound() {
    when(messageThreadRepository.thread(id("thread-id"))).thenReturn(empty());

    service.thread(new User().setId(id("user id")), id("thread-id"));
  }

  @Test
  public void createMessageThreadForAd() {
    User user = new User().setId(id("user id"));
    User counterparty = new User().setId(id("counterparty id"));
    when(adService.ad(id("ad-id"))).thenReturn(new Ad().setTitle("Title").setCreatedBy(id("ad publisher")));
    MessageThread newThread = new MessageThread();
    when(messageThreadRepository.create(messageThreadArgumentCaptor.capture())).thenReturn(newThread);
    when(userService.user(id("ad publisher"))).thenReturn(counterparty);

    MessageThread result = service.createMessageThreadForAd(user, id("ad-id"));

    assertThat(result).isEqualTo(newThread);
    MessageThread createdThread = messageThreadArgumentCaptor.getValue();
    assertThat(createdThread.getAdId()).isEqualTo(id("ad-id"));
    assertThat(createdThread.getCreatedBy()).isEqualTo(id("user id"));
    assertThat(createdThread.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
    assertThat(createdThread.isGroup()).isFalse();
    assertThat(createdThread.getParties()).extracting("user").containsExactly(counterparty, user);
  }

  @Test
  public void messageThreadView() {
    User user = new User().setId(id("myself"));
    User counterparty = new User().setId(id("counterparty"));
    Message message = new Message().setId(33L);
    Instant now = now();
    MessageThread messageThread = new MessageThread()
      .setId(id("thread id"))
      .setTitle("title")
      .setAdId(id("ad id"))
      .setGroup(true)
      .setCreatedAt(now)
      .setCreatedBy(user.getId())
      .setParties(asList(party(user), party(counterparty)));
    UserView conterpartyView = new UserView();
    when(userService.view(counterparty)).thenReturn(conterpartyView);
    UserView userView = new UserView();
    when(userService.view(user)).thenReturn(userView);
    MessageView messageView = new MessageView();
    doReturn(messageView).when(service).view(message);
    when(messageRepository.lastMessage(id("thread id"))).thenReturn(Optional.of(message));
    AdView adView = new AdView();
    when(adService.view(user, id("ad id"))).thenReturn(adView);

    MessageThreadView view = service.view(user, messageThread);

    assertThat(view.getId()).isEqualTo(id("thread id"));
    assertThat(view.getAd()).isEqualTo(adView);
    assertThat(view.getTitle()).isEqualTo("title");
    assertThat(view.getParties()).containsExactly(conterpartyView);
    assertThat(view.getLastMessage()).isEqualTo(messageView);
    assertThat(view.isUnread()).isEqualTo(false);
    assertThat(view.isGroup()).isEqualTo(true);
    assertThat(view.getCreatedAt()).isEqualTo(now);
    assertThat(view.getCreator()).isEqualTo(userView);
    assertThat(view.getCounterParty()).isEqualTo(conterpartyView);
  }

  @Test
  public void messageView() {
    Instant messageCreated = now();
    Message message = new Message()
      .setId(33L)
      .setThreadId(id("thread id"))
      .setCreatedAt(messageCreated)
      .setCreatedBy(id("user id"))
      .setText("hello");

    MessageView result = service.view(message);

    assertThat(result).isEqualTo(new MessageView()
      .setId(33L)
      .setCreatedAt(messageCreated)
      .setCreatedBy(id("user id"))
      .setText("hello"));
  }

  @Test
  public void threads() {
    User user = new User();
    MessageThread thread = new MessageThread().setId(id("unread"));
    when(messageThreadRepository.listByUser(user)).thenReturn(asList(thread));
    MessageThreadView view = new MessageThreadView().setId(id("unread")).setLastMessage(new MessageView());
    doReturn(view).when(service).view(user, thread);
    when(messageThreadRepository.unreadMessageThreadIds(user)).thenReturn(asList(id("unread")));

    List<MessageThreadView> result = service.threads(user);

    assertThat(result).containsExactly(view);
    assertThat(view.isUnread()).isTrue();
  }

  @Test
  public void threads_excludePrivateThreadsWithoutMessages() {
    User user = new User();
    MessageThread thread = new MessageThread();
    when(messageThreadRepository.listByUser(user)).thenReturn(asList(thread));
    MessageThreadView threadView = new MessageThreadView();
    doReturn(threadView).when(service).view(user, thread);

    assertThat(service.threads(user)).isEmpty();
  }

  @Test
  public void threads_includesGroupThreadsWithoutMessages() {
    User user = new User();
    MessageThread thread = new MessageThread().setGroup(true);
    when(messageThreadRepository.listByUser(user)).thenReturn(asList(thread));
    MessageThreadView threadView = new MessageThreadView().setGroup(true);
    doReturn(threadView).when(service).view(user, thread);

    assertThat(service.threads(user)).isNotEmpty();
  }

  @Test
  public void ordersNewestThreadsFirst() {
    MessageThreadView view1 = new MessageThreadView().setLastMessage(new MessageView().setCreatedAt(now().minus(2, HOURS)));
    MessageThreadView view2 = new MessageThreadView().setLastMessage(new MessageView().setCreatedAt(now().minus(1, HOURS)));
    MessageThreadView view3 = new MessageThreadView().setLastMessage(new MessageView().setCreatedAt(now().minus(3, HOURS)));
    List<MessageThreadView> data = new ArrayList<>(asList(view1, view2, view3));

    service.sortAsNewestFirst(data);

    assertThat(data).containsExactly(view2, view1, view3);
  }

  @Test
  public void ordersNewestThreadsFirst_usesCreatedAtForGroupsWithoutMessages() {
    MessageThreadView view1 = new MessageThreadView().setLastMessage(new MessageView().setCreatedAt(now().minus(2, HOURS)));
    MessageThreadView view2 = new MessageThreadView().setCreatedAt(now().minus(1, HOURS)).setGroup(true);
    MessageThreadView view3 = new MessageThreadView().setLastMessage(new MessageView().setCreatedAt(now().minus(3, HOURS)));
    List<MessageThreadView> data = new ArrayList<>(asList(view1, view2, view3));

    service.sortAsNewestFirst(data);

    assertThat(data).containsExactly(view2, view1, view3);
  }

  @Test
  public void postMessage() {
    User user = new User().setId(id("user id"));
    when(messageThreadRepository.thread(id("thread id"))).thenReturn(Optional.of(new MessageThread().setParties(asList(party(user)))));
    Message createdMessage = new Message();
    when(messageRepository.create(any())).thenReturn(createdMessage);
    MessageView messageView = new MessageView();
    doReturn(messageView).when(service).view(createdMessage);

    MessageView result = service.postMessage(user, new MessagePostCommand().setThreadId(id("thread id")).setText("message text"));

    assertThat(result).isSameAs(messageView);
    ArgumentCaptor<Message> messageArgument = ArgumentCaptor.forClass(Message.class);
    verify(messageRepository).create(messageArgument.capture());
    Message message = messageArgument.getValue();
    assertThat(message.getThreadId()).isEqualTo(id("thread id"));
    assertThat(message.getCreatedBy()).isEqualTo(id("user id"));
    assertThat(message.getText()).isEqualTo("message text");
    assertThat(message.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
  }

  @Test
  public void postMessage_notifiesParties() {
    User sendingUser = new User().setId(id("user id")).setUsername("sender").setPushNotificationToken("sending user token");
    User otherUser = new User().setId(id("user id")).setPushNotificationToken("other user token");
    List<MessageThreadParty> parties = asList(party(sendingUser), party(otherUser));
    when(messageThreadRepository.thread(id("thread id"))).thenReturn(Optional.of(new MessageThread().setId(id("thread id")).setParties(parties)));
    when(messageRepository.create(any())).thenReturn(new Message().setText("Hello"));
    when(userService.fullName(sendingUser)).thenReturn("John Doe");

    service.postMessage(sendingUser, new MessagePostCommand().setThreadId(id("thread id")).setText("message text"));

    ImmutableMap<String, String> params = ImmutableMap.of("type", "new_message", "threadId", Long.toString(id("thread id")));
    verify(pushNotificationService).send(otherUser, "John Doe:\n\nHello", params);
    verifyNoMoreInteractions(pushNotificationService);
  }

  @Test(expected = ForbiddenException.class)
  public void create_canPostOnlyToThreadParticipatingIn() {
    when(messageThreadRepository.thread(id("thread id"))).thenReturn(Optional.of(new MessageThread().setParties(asList(party(new User().setId(id("other user")))))));

    service.postMessage(new User().setId(id("user id")), new MessagePostCommand().setThreadId(id("thread id")).setText("message text"));
  }

  @Test
  public void messages() {
    Message message = new Message();
    when(messageRepository.listByThread(id("thread id"))).thenReturn(asList(message));
    MessageView view = new MessageView();
    doReturn(view).when(service).view(message);
    MessageThreadParty party = party(new User().setId(id("user id")));
    when(messageThreadRepository.thread((id("thread id")))).thenReturn(Optional.of(new MessageThread().setParties(asList(party))));

    List<MessageView> result = service.messages(new User().setId(id("user id")), id("thread id"));

    assertThat(result).containsExactly(view);
    verify(messageThreadRepository).update(party);
    assertThat(party.getVisitedAt()).isCloseTo(now(), within(1, SECONDS));
  }

  @Test(expected = BadRequestException.class)
  public void message_threadMustExist() {
    when(messageThreadRepository.thread((id("thread id")))).thenReturn(empty());

    service.messages(new User().setId(id("me")), id("thread id"));
  }

  @Test(expected = ForbiddenException.class)
  public void message_canBeAccessedByParticipatingParty() {
    MessageThread messageThread = new MessageThread().setParties(asList(party(new User().setId(id("other user")))));
    when(messageThreadRepository.thread((id("thread id")))).thenReturn(Optional.of(messageThread));

    service.messages(new User().setId(id("me")), id("thread id"));
  }

  @Test
  public void unreadMessageThreadCount() {
    User user = new User();
    when(messageThreadRepository.unreadMessageThreadIds(user)).thenReturn(asList(1L, 2L, 3L));

    int result = service.unreadMessageThreadCount(user);

    assertThat(result).isEqualTo(3);
  }

  @Test
  public void group() {
    User addedUser = new User().setId(id("addedUser"));
    doReturn(asList(addedUser)).when(service).validatePartiesCommunity(id("community"), asList(id("addedUser")));

    MessageThread createdThread = new MessageThread();
    MessageThreadView threadView = new MessageThreadView();
    when(messageThreadRepository.create(messageThreadArgumentCaptor.capture())).thenReturn(createdThread);

    User creatingUser = new User().setId(id("creatingUser")).setCommunityId(id("community"));
    doReturn(threadView).when(service).view(creatingUser, createdThread);


    CreateGroupCommand command = new CreateGroupCommand().setTitle("hello").setMemberIds(asList(id("addedUser")));
    MessageThreadView result = service.group(creatingUser, command);


    assertThat(result).isSameAs(threadView);

    MessageThread realThread = messageThreadArgumentCaptor.getValue();
    assertThat(realThread.isGroup()).isTrue();
    assertThat(realThread.getTitle()).isEqualTo("hello");
    assertThat(realThread.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
    assertThat(realThread.getCreatedBy()).isEqualTo(id("creatingUser"));
    assertThat(realThread.getParties()).extracting("user").containsExactly(addedUser, creatingUser);
  }

  @Test
  public void updateGroup() {
    User existingUser = new User().setId(id("existingUser")).setCommunityId(id("community"));
    User addedUser = new User().setId(id("addedUser")).setCommunityId(id("community"));
    doReturn(asList(existingUser, addedUser)).when(service).validatePartiesCommunity(id("community"), list(id("existingUser"), id("addedUser")));

    MessageThread originalThread = new MessageThread()
      .setId(id("thread"))
      .setGroup(true).setTitle("Hello")
      .setCreatedBy(existingUser.getId())
      .setParties(list(party(existingUser)));
    when(messageThreadRepository.thread(id("thread"))).thenReturn(Optional.of(originalThread));


    GroupMessageThreadUpdateCommand command = new GroupMessageThreadUpdateCommand()
      .setThreadId(id("thread"))
      .setTitle("Hola!")
      .setMemberIds(list(id("existingUser"), id("addedUser")));

    MessageThreadView messageThreadView = new MessageThreadView();
    doReturn(messageThreadView).when(service).view(eq(existingUser), messageThreadArgumentCaptor2.capture());

    MessageThreadView result = service.updateGroup(existingUser, command);


    assertThat(result).isSameAs(messageThreadView);

    verify(messageThreadRepository).update(messageThreadArgumentCaptor.capture());
    MessageThread updatedThread = messageThreadArgumentCaptor.getValue();
    assertThat(updatedThread.getTitle()).isEqualTo("Hola!");
    assertThat(updatedThread.getParties()).extracting("user").containsExactly(existingUser, addedUser);
    assertThat(updatedThread).isSameAs(messageThreadArgumentCaptor2.getValue());
  }

  @Test(expected = ForbiddenException.class)
  public void updateGroup_updaterMustBelongToThread() {
    User notMemberUser = new User().setId(id("notMemberUser")).setCommunityId(id("community"));
    User existingUser = new User().setId(id("existingUser")).setCommunityId(id("community"));
    MessageThread originalThread = new MessageThread().setId(id("thread")).setGroup(true).setParties(list(party(existingUser)));
    when(messageThreadRepository.thread(id("thread"))).thenReturn(Optional.of(originalThread));

    GroupMessageThreadUpdateCommand command = new GroupMessageThreadUpdateCommand().setThreadId(id("thread")).setMemberIds(list(id("existingUser")));
    service.updateGroup(notMemberUser, command);
  }

  @Test(expected = BadRequestException.class)
  public void updateGroup_threadMustBeGroup() {
    User user = new User().setId(id("creatingUser")).setCommunityId(id("community"));

    MessageThread updatedThread = new MessageThread().setGroup(false).setParties(list(new MessageThreadParty().setUser(user)));
    when(messageThreadRepository.thread(id("thread"))).thenReturn(Optional.of(updatedThread));

    service.updateGroup(user, new GroupMessageThreadUpdateCommand().setThreadId(id("thread")).setMemberIds(list(id("addedUser"))));
  }

  private <T> ArrayList<T> list(T... elements) {
    return new ArrayList<T>(asList(elements));
  }

  @Test
  public void validatePartiesCommunity() {
    User user1 = new User().setCommunityId(id("community"));
    User user2 = new User().setCommunityId(id("community"));
    when(userService.user(id("user1"))).thenReturn(user1);
    when(userService.user(id("user2"))).thenReturn(user2);

    List<User> result = service.validatePartiesCommunity(id("community"), asList(id("user1"), id("user2")));

    assertThat(result).containsExactly(user1, user2);
  }

  @Test(expected = ForbiddenException.class)
  public void validatePartiesCommunity_requiresSameCommunity() {
    when(userService.user(id("user"))).thenReturn(new User().setCommunityId(id("other community")));

    service.validatePartiesCommunity(id("community"), asList(id("user")));
  }

  @Test(expected = BadRequestException.class)
  public void validatePartiesCommunity_requiresUser() {
    service.validatePartiesCommunity(id("community"), emptyList());
  }
}