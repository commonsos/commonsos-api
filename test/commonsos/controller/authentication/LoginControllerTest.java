package commonsos.controller.authentication;

import commonsos.AuthenticationException;
import commonsos.GsonProvider;
import commonsos.domain.user.Session;
import commonsos.domain.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginControllerTest {

  @Mock Request request;
  @Mock UserService service;
  @InjectMocks LoginController controller;

  @Before
  public void setUp() throws Exception {
    controller.gson = new GsonProvider().get();
  }

  @Test
  public void handle() throws Exception {
    when(service.login("user", "pwd")).thenReturn(new Session().setToken("auth token"));
    when(request.body()).thenReturn("{\"username\": \"user\", \"password\": \"pwd\"}");

    Session session = controller.handle(request, null);

    assertThat(session.getToken()).isEqualTo("auth token");
  }

  @Test(expected = AuthenticationException.class)
  public void handleNoContent() throws Exception {
    when(service.login("user", "pwd")).thenThrow(new AuthenticationException());
    when(request.body()).thenReturn("{\"username\": \"user\", \"password\": \"pwd\"}");

    controller.handle(request, null);
  }
}