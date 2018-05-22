package commonsos.controller.auth;

import commonsos.AuthenticationException;
import commonsos.CsrfFilter;
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

import java.util.HashSet;
import java.util.Set;

import static commonsos.CsrfFilter.CSRF_TOKEN_COOKIE_NAME;
import static commonsos.LogFilter.USERNAME_MDC_KEY;
import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

  @Mock Request request;
  @Mock Response response;
  @Mock Session session;
  @Mock UserService service;
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
    doReturn("random value").when(controller).generateCsrfToken();

    UserPrivateView result = controller.handle(request, response);

    verify(response).cookie("/", CSRF_TOKEN_COOKIE_NAME, "random value", -1, false);
    verify(session).attribute(USER_SESSION_ATTRIBUTE_NAME, user);
    verify(session).attribute(CsrfFilter.CSRF_TOKEN_SESSION_ATTRIBUTE_NAME, "random value");
    assertThat(result).isSameAs(userView);
    assertThat(MDC.get(USERNAME_MDC_KEY)).isEqualTo("john");
  }

  @Test(expected = AuthenticationException.class)
  public void handle_loginFails() throws Exception {
    when(service.checkPassword("user", "pwd")).thenThrow(new AuthenticationException());
    when(request.body()).thenReturn("{\"username\": \"user\", \"password\": \"pwd\"}");

    controller.handle(request, null);
  }

  @Test
  public void csrfTokenIsRandomLongString() {
    Set<String> tokens = new HashSet<>();
    for(int i = 0; i < 100; i++)
      tokens.add(controller.generateCsrfToken());

    assertThat(tokens).hasSize(100);
    assertThat(tokens.iterator().next().length()).isGreaterThan(20);
  }
}