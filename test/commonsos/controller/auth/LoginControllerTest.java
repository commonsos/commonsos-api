package commonsos.controller.auth;

import commonsos.AuthenticationException;
import commonsos.GsonProvider;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

  @Mock Request request;
  @Mock Session session;
  @Mock UserService service;
  @InjectMocks LoginController controller;

  @Before
  public void setUp() throws Exception {
    controller.gson = new GsonProvider().get();
    when(request.session()).thenReturn(session);
  }

  @Test
  public void handle() throws Exception {
    User user = new User();
    when(service.checkPassword("user", "pwd")).thenReturn(user);
    when(request.body()).thenReturn("{\"username\": \"user\", \"password\": \"pwd\"}");
    UserView userView = new UserView();
    when(service.view(user)).thenReturn(userView);

    UserView result = controller.handle(request, null);

    verify(session).attribute("user", user);
    assertThat(result).isSameAs(userView);
  }

  @Test(expected = AuthenticationException.class)
  public void handle_loginFails() throws Exception {
    when(service.checkPassword("user", "pwd")).thenThrow(new AuthenticationException());
    when(request.body()).thenReturn("{\"username\": \"user\", \"password\": \"pwd\"}");

    controller.handle(request, null);
  }
}