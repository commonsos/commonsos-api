package commonsos.controller.auth;

import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

  @InjectMocks UserController controller;
  @Mock UserService service;

  @Test
  public void handle() {
    UserView userView = new UserView();
    when(service.view(new User())).thenReturn(userView);

    UserView result = controller.handle(new User(), null, null);

    assertThat(result).isEqualTo(userView);
  }
}