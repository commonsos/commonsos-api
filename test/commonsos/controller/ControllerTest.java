package commonsos.controller;

import commonsos.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Response;
import spark.Session;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

  @Mock Request request;
  @Mock Session session;
  @Mock  Response response;

  @Test
  public void suppliesUserParameter() {
    User user = new User().setId("user id");
    when(session.attribute("user")).thenReturn(user);
    when(request.session()).thenReturn(session);
    SampleController controller = spy(new SampleController());

    controller.handle(request, response);

    verify(controller).handle(user, request, response);
  }

  private static class SampleController extends Controller {

    @Override protected Object handle(User user, Request request, Response response) {
      return null;
    }
  }
}