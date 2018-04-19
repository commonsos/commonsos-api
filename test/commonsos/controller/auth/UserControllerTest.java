package commonsos.controller.auth;

import commonsos.domain.auth.User;
import commonsos.domain.auth.UserPrivateView;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

  @InjectMocks UserController controller;
  @Mock UserService service;
  @Mock Request request;

  @Test
  public void handle() {
    UserPrivateView userView = new UserPrivateView();
    when(service.privateView(new User())).thenReturn(userView);

    Object result = controller.handle(new User(), request, null);

    assertThat(result).isEqualTo(userView);
  }

  @Test
  public void handle_withOtherUserId() {
    when(request.params("id")).thenReturn("123");
    UserView userView = new UserView();
    when(service.view("123")).thenReturn(userView);

    Object result = controller.handle(new User().setAdmin(false), request, null);

    assertThat(result).isEqualTo(userView);
  }

  @Test
  public void handle_withOtherUserId_admin() {
    when(request.params("id")).thenReturn("123");
    UserPrivateView userView = new UserPrivateView();
    User user = new User().setAdmin(true);
    when(service.privateView(user, "123")).thenReturn(userView);

    Object result = controller.handle(user, request, null);

    assertThat(result).isEqualTo(userView);
  }
}