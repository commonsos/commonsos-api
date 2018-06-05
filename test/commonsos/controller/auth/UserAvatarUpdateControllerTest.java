package commonsos.controller.auth;

import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserAvatarUpdateControllerTest {

  @Mock UserService userService;
  @Mock User user;
  @Mock Request request;
  @InjectMocks @Spy UserAvatarUpdateController controller;

  @Test
  public void handle() {
    InputStream image = mock(InputStream.class);
    doReturn(image).when(controller).image(request);
    when(userService.updateAvatar(user, image)).thenReturn("/url");

    String result = controller.handle(user, request, null);

    assertThat(result).isEqualTo("/url");
  }
}