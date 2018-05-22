package commonsos.controller.auth;

import commonsos.AuthenticationException;
import commonsos.Csrf;
import commonsos.GsonProvider;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserPrivateView;
import commonsos.domain.auth.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.MDC;
import spark.Request;
import spark.Response;
import spark.Session;

import static commonsos.LogFilter.USERNAME_MDC_KEY;
import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

  @Mock Request request;
  @Mock Response response;
  @Mock Session session;
  @Mock UserService service;
  @Mock Csrf csrf;
  @InjectMocks @Spy LoginController controller;

  @Before
  public void setUp() throws Exception {
    controller.gson = new GsonProvider().get();
    when(request.session()).thenReturn(session);
  }

  @Test
  public void handle() {
    User user = new User().setUsername("john");
    when(service.checkPassword("john", "pwd")).thenReturn(user);
    when(request.body()).thenReturn("{\"username\": \"john\", \"password\": \"pwd\"}");
    UserPrivateView userView = new UserPrivateView();
    when(service.privateView(user)).thenReturn(userView);

    UserPrivateView result = controller.handle(request, response);


    verify(csrf).setToken(request, response);
    verify(session).attribute(USER_SESSION_ATTRIBUTE_NAME, user);
    assertThat(result).isSameAs(userView);
    assertThat(MDC.get(USERNAME_MDC_KEY)).isEqualTo("john");
  }

  @Test(expected = AuthenticationException.class)
  public void handle_loginFails() throws Exception {
    when(service.checkPassword("user", "pwd")).thenThrow(new AuthenticationException());
    when(request.body()).thenReturn("{\"username\": \"user\", \"password\": \"pwd\"}");

    controller.handle(request, null);
  }
}