package commonsos.controller.auth;

import commonsos.GsonProvider;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserPrivateView;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserUpdateCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserUpdateControllerTest {

  @InjectMocks UserUpdateController controller;
  @Mock Request request;
  @Mock UserService userService;
  @Mock User user;

  @Before
  public void setGson() {
    controller.gson = new GsonProvider().get();
  }

  @Test
  public void handle() {
    String json = "{\"id\":3,\"firstName\":\"John\",\"lastName\":\"Doe\",\"description\":\"Retired\", \"location\":\"Sapporo\"}";
    when(request.body()).thenReturn(json);
    User updatedUser = new User();
    when(userService.updateUser(any(), any())).thenReturn(updatedUser);
    UserPrivateView privateView = new UserPrivateView();
    when(userService.privateView(updatedUser)).thenReturn(privateView);
    UserPrivateView result = controller.handle(user, request, null);

    assertThat(result).isEqualTo(privateView);
    UserUpdateCommand expectedCommand = new UserUpdateCommand().setFirstName("John").setLastName("Doe").setDescription("Retired").setLocation("Sapporo");
    verify(userService).updateUser(user, expectedCommand);
  }
}