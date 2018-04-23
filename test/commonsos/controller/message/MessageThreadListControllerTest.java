package commonsos.controller.message;

import commonsos.domain.auth.User;
import commonsos.domain.message.MessageService;
import commonsos.domain.message.MessageThreadView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageThreadListControllerTest {

  @InjectMocks MessageThreadListController controller;
  @Mock MessageService service;

  @Test
  public void handle() {
    User user = new User();
    MessageThreadView messageThreadView = new MessageThreadView();
    when(service.threads(user)).thenReturn(asList(messageThreadView));

    List<MessageThreadView> threads = controller.handle(user, null, null);

    assertThat(threads).containsExactly(messageThreadView);
  }
}