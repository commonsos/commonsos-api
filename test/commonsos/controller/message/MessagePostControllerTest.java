package commonsos.controller.message;

import commonsos.GsonProvider;
import commonsos.domain.auth.User;
import commonsos.domain.message.MessagePostCommand;
import commonsos.domain.message.MessageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessagePostControllerTest {

  @Mock MessageService service;
  @Mock Request request;
  @InjectMocks MessagePostController controller;

  @Before
  public void setUp() throws Exception {
    controller.gson = new GsonProvider().get();
  }

  @Test
  public void handle() {
    User user = new User();
    when(request.body()).thenReturn("{\"threadId\": \"thread id\", \"text\": \"message text\"}");

    controller.handle(user, request, null);

    verify(service).postMessage(user, new MessagePostCommand().setThreadId("thread id").setText("message text"));
  }
}