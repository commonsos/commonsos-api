package commonsos.controller.auth;

import commonsos.GsonProvider;
import commonsos.domain.auth.MobileDeviceUpdateCommand;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserMobileDeviceUpdateControllerTest {

  @InjectMocks UserMobileDeviceUpdateController controller;
  @Mock Request request;
  @Mock UserService userService;
  @Mock User user;

  @Before
  public void setGson() {
    controller.gson = new GsonProvider().get();
  }

  @Test
  public void handle() {
    when(request.body()).thenReturn("{\"pushNotificationToken\":\"12345\"}");

    controller.handle(user, request, null);

    MobileDeviceUpdateCommand expectedCommand = new MobileDeviceUpdateCommand().setPushNotificationToken("12345");
    verify(userService).updateMobileDevice(user, expectedCommand);
  }
}