package commonsos.controller.message;

import commonsos.domain.auth.User;
import commonsos.domain.message.MessageService;
import commonsos.domain.message.MessageView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageListControllerTest {

  @InjectMocks MessageListController controller;
  @Mock MessageService service;
  @Mock Request request;

  @Test
  public void handle() {
    User user = new User();
    List<MessageView> messages = new ArrayList<>();
    when(request.params("id")).thenReturn("thread id");
    when(service.messages(user, "thread id")).thenReturn(messages);

    List<MessageView> result = controller.handle(user, request, null);

    assertThat(result).isSameAs(messages);
  }
}