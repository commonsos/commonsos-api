package commonsos.domain.message;

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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
  @InjectMocks @Spy MessageService service;

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
    when(messageThreadRepository.byAdId(user, id("ad-id"))).thenReturn(Optional.empty());

    MessageThread newThread = new MessageThread();
    doReturn(newThread).when(service).createMessageThreadForAd(user, id("ad-id"));

    MessageThreadView messageThreadView = new MessageThreadView();
    doReturn(messageThreadView).when(service).view(user, newThread);


    MessageThreadView result = service.threadForAd(user, id("ad-id"));


    assertThat(result).isEqualTo(messageThreadView);
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
    when(messageThreadRepository.thread(id("thread-id"))).thenReturn(Optional.empty());

    service.thread(new User().setId(id("user id")), id("thread-id"));
  }

  @Test
  public void createMessageThreadForAd() {
    User user = new User().setId(id("user id"));
    User counterparty = new User().setId(id("counterparty id"));
    when(adService.ad(id("ad-id"))).thenReturn(new Ad().setTitle("Title").setCreatedBy(id("ad publisher")));
    MessageThread newThread = new MessageThread();
    when(messageThreadRepository.create(any(MessageThread.class))).thenReturn(newThread);
    when(userService.user(id("ad publisher"))).thenReturn(counterparty);

    MessageThread result = service.createMessageThreadForAd(user, id("ad-id"));

    assertThat(result).isEqualTo(newThread);
    MessageThread messageThread = new MessageThread().setAdId(id("ad-id")).setCreatedBy(id("user id")).setTitle("Title").setParties(asList(party(counterparty), party(user)));
    verify(messageThreadRepository).create(messageThread);
  }

  @Test
  public void messageThreadView() {
    User user = new User().setId(id("myself"));
    User counterparty = new User().setId(id("counterparty"));
    Message message = new Message().setId(33L);
    MessageThread messageThread = new MessageThread()
      .setId(id("thread id"))
      .setTitle("title")
      .setAdId(id("ad id"))
      .setParties(asList(party(user), party(counterparty)));
    UserView conterpartyView = new UserView();
    when(userService.view(counterparty)).thenReturn(conterpartyView);
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
  public void threads_excludeWithoutMessages() {
    User user = new User();
    MessageThread thread = new MessageThread();
    when(messageThreadRepository.listByUser(user)).thenReturn(asList(thread));
    MessageThreadView threadView = new MessageThreadView();
    doReturn(threadView).when(service).view(user, thread);

    assertThat(service.threads(user)).isEmpty();
  }

  @Test
  public void ordersLatestThreadsFirst() {
    MessageThreadView view1 = new MessageThreadView().setLastMessage(new MessageView().setCreatedAt(now().minus(2, HOURS)));
    MessageThreadView view2 = new MessageThreadView().setLastMessage(new MessageView().setCreatedAt(now().minus(1, HOURS)));
    MessageThreadView view3 = new MessageThreadView().setLastMessage(new MessageView().setCreatedAt(now().minus(3, HOURS)));
    List<MessageThreadView> data = new ArrayList<>(asList(view1, view2, view3));

    service.sortThreadsByLastMessageTime(data);

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
    when(messageThreadRepository.thread((id("thread id")))).thenReturn(Optional.empty());

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
}