package commonsos.controller.auth;

import com.google.gson.Gson;
import commonsos.Csrf;
import commonsos.domain.auth.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Response;
import spark.Session;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountCreateControllerTest {

  @Mock Request request;
  @Mock Response response;
  @Mock Session session;
  @Mock Csrf csrf;
  @Mock UserService userService;
  @InjectMocks AccountCreateController controller;

  @Before
  public void setUp() throws Exception {
    controller.gson = new Gson();
    when(request.session()).thenReturn(session);
  }

  @Test
  public void setsCsrfToken() {
    controller.handle(request, response);

    verify(csrf).setToken(request, response);
  }
}