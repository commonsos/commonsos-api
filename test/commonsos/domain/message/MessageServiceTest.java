package commonsos.domain.message;

import commonsos.BadRequestException;
import commonsos.ForbiddenException;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.OffsetDateTime.now;
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
    when(messageThreadRepository.byAdId(user, "ad-id")).thenReturn(Optional.of(messageThread));
    MessageThreadView messageThreadView = new MessageThreadView();
    doReturn(messageThreadView).when(service).view(user, messageThread);

    MessageThreadView result = service.threadForAd(user, "ad-id");

    assertThat(result).isSameAs(messageThreadView);
  }

  @Test
  public void threadForAd_createNewIfNotPresent() {
    User user = new User().setId("user id");
    when(messageThreadRepository.byAdId(user, "ad-id")).thenReturn(Optional.empty());

    MessageThread newThread = new MessageThread();
    doReturn(newThread).when(service).createMessageThreadForAd(user, "ad-id");

    MessageThreadView messageThreadView = new MessageThreadView();
    doReturn(messageThreadView).when(service).view(user, newThread);


    MessageThreadView result = service.threadForAd(user, "ad-id");


    assertThat(result).isEqualTo(messageThreadView);
  }

  @Test
  public void thread() {
    User user = new User().setId("user id");
    MessageThread messageThread = new MessageThread().setUsers(asList(user));
    when(messageThreadRepository.thread("thread-id")).thenReturn(Optional.of(messageThread));
    MessageThreadView messageThreadView = new MessageThreadView();
    doReturn(messageThreadView).when(service).view(user, messageThread);

    assertThat(service.thread(user, "thread-id")).isSameAs(messageThreadView);
    verify(service).loadMessages(messageThread);
  }

  @Test(expected = ForbiddenException.class)
  public void thread_canOnlyAccessThreadParticipatingIn() {
    MessageThread messageThread = new MessageThread().setUsers(asList(new User().setId("other user")));
    when(messageThreadRepository.thread("thread-id")).thenReturn(Optional.of(messageThread));

    service.thread(new User().setId("user id"), "thread-id");
  }

  @Test(expected = BadRequestException.class)
  public void thread_notFound() {
    when(messageThreadRepository.thread("thread-id")).thenReturn(Optional.empty());

    service.thread(new User().setId("user id"), "thread-id");
  }

  @Test
  public void createMessageThreadForAd() {
    User user = new User().setId("user id");
    User counterparty = new User().setId("counterparty id");
    when(adService.ad("ad-id")).thenReturn(new Ad().setTitle("Title").setCreatedBy("ad publisher"));
    MessageThread newThread = new MessageThread();
    when(messageThreadRepository.create(any(MessageThread.class))).thenReturn(newThread);
    when(userService.user("ad publisher")).thenReturn(counterparty);

    MessageThread result = service.createMessageThreadForAd(user, "ad-id");

    assertThat(result).isEqualTo(newThread);
    MessageThread messageThread = new MessageThread().setAdId("ad-id").setCreatedBy("user id").setTitle("Title").setUsers(asList(counterparty, user));
    verify(messageThreadRepository).create(messageThread);
  }

  @Test
  public void view() {
    User user = new User().setId("myself");
    User counterparty = new User().setId("counterparty");
    Message message = new Message().setId("33");
    MessageThread messageThread = new MessageThread()
      .setId("thread id")
      .setTitle("title")
      .setUsers(asList(user, counterparty))
      .setMessages(asList(message));
    UserView conterpartyView = new UserView();
    when(userService.view(counterparty)).thenReturn(conterpartyView);
    MessageView messageView = new MessageView().setId("33");
    when(service.view(message)).thenReturn(messageView);

    MessageThreadView view = service.view(user, messageThread);

    assertThat(view.getId()).isEqualTo("thread id");
    assertThat(view.getTitle()).isEqualTo("title");
    assertThat(view.getUsers()).containsExactly(conterpartyView);
    assertThat(view.getMessages()).containsExactly(messageView);
  }

  @Test
  public void messageView() {
    OffsetDateTime messageCreated = now();
    Message message = new Message().setId("33").setThreadId("thread id").setCreatedAt(messageCreated).setCreatedBy("myself").setText("hello");

    MessageView result = service.view(message);

    assertThat(result).isEqualTo(new MessageView().setId("33").setCreatedAt(messageCreated).setCreatedBy("myself").setText("hello"));
  }

  @Test
  public void threads() {
    User user = new User();
    MessageThread thread = new MessageThread();
    when(messageThreadRepository.listByUser(user)).thenReturn(asList(thread));
    MessageThreadView threadView = new MessageThreadView();
    doReturn(threadView).when(service).view(user, thread);

    List<MessageThreadView> result = service.threads(user);

    assertThat(result).containsExactly(threadView);
    verify(service).loadMessages(thread);
  }

  @Test
  public void loadMessages() {
    Message message = new Message().setId("1");
    when(messageRepository.listByThread("thread id")).thenReturn(asList(message));
    MessageThread thread = new MessageThread().setId("thread id");

    service.loadMessages(thread);

    assertThat(thread.getMessages()).containsExactly(message);
  }

  @Test
  public void create() {
    User user = new User().setId("user id");
    when(messageThreadRepository.thread("thread id")).thenReturn(Optional.of(new MessageThread().setUsers(asList(user))));

    service.postMessage(user, new MessagePostCommand().setThreadId("thread id").setText("message text"));

    ArgumentCaptor<Message> messageArgument = ArgumentCaptor.forClass(Message.class);
    verify(messageRepository).create(messageArgument.capture());
    Message message = messageArgument.getValue();
    assertThat(message.getThreadId()).isEqualTo("thread id");
    assertThat(message.getCreatedBy()).isEqualTo("user id");
    assertThat(message.getText()).isEqualTo("message text");
    assertThat(message.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
  }

  @Test(expected = ForbiddenException.class)
  public void create_canPostOnlyToThreadParticipatingIn() {
    when(messageThreadRepository.thread("thread id")).thenReturn(Optional.of(new MessageThread().setUsers(asList(new User().setId("other user")))));

    service.postMessage(new User().setId("user id"), new MessagePostCommand().setThreadId("thread id").setText("message text"));
  }
}