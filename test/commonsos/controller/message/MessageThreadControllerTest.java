package commonsos.controller.message;

import commonsos.domain.auth.User;
import commonsos.domain.message.MessageService;
import commonsos.domain.message.MessageThreadView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageThreadControllerTest {

  @InjectMocks MessageThreadController controller;
  @Mock Request request;
  @Mock MessageService service;

  @Test
  public void handle() {
    User user = new User();
    when(request.params("id")).thenReturn("thread-id");
    MessageThreadView view = new MessageThreadView();
    when(service.thread(user, "thread-id")).thenReturn(view);

    MessageThreadView result = controller.handle(user, request, null);

    assertThat(result).isEqualTo(view);
  }
}