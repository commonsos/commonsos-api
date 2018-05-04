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
public class MessageThreadForAdControllerTest {

  @InjectMocks MessageThreadForAdController controller;
  @Mock Request request;
  @Mock MessageService service;

  @Test
  public void handle() {
    User user = new User();
    when(request.params("adId")).thenReturn("123");
    MessageThreadView view = new MessageThreadView();
    when(service.threadForAd(user, 123L)).thenReturn(view);

    MessageThreadView result = controller.handle(user, request, null);

    assertThat(result).isEqualTo(view);
  }
}