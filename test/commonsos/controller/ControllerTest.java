package commonsos.controller;

import commonsos.UserSession;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Response;
import spark.Session;

import static commonsos.TestId.id;
import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

  @Mock Request request;
  @Mock Session session;
  @Mock Response response;
  @Mock UserService userService;
  @InjectMocks @Spy SampleController controller;

  @Test
  public void suppliesUserParameter() {
    User user = new User();
    when(session.attribute(USER_SESSION_ATTRIBUTE_NAME)).thenReturn(new UserSession().setUserId(id("user id")));
    when(request.session()).thenReturn(session);
    when(userService.user(id("user id"))).thenReturn(user);

    controller.handle(request, response);

    verify(controller).handle(user, request, response);
  }

  private static class SampleController extends Controller {

    @Override protected Object handle(User user, Request request, Response response) {
      return null;
    }
  }
}