package commonsos.controller.message;

import commonsos.domain.auth.User;
import commonsos.domain.message.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageThreadUnreadCountControllerTest {

  @InjectMocks MessageThreadUnreadCountController controller;
  @Mock MessageService service;

  @Test
  public void handle() {
    User user = new User();
    when(service.unreadMessageThreadCount(user)).thenReturn(3);

    Map<String, Object> result = controller.handle(user, null, null);

    assertThat(result.get("count")).isEqualTo(3);
  }
}