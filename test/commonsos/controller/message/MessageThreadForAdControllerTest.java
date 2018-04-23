package commonsos.controller.message;

import commonsos.domain.auth.User;
import commonsos.domain.message.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageThreadForAdControllerTest {

  @InjectMocks MessageThreadForAdController controller;
  @Mock Request request;
  @Mock MessageService service;

  @Test
  public void handle() {
    User user = new User();
    when(request.params("adId")).thenReturn("ad-id");

    controller.handle(user, request, null);

    verify(service).thread(user, "ad-id");
  }
}