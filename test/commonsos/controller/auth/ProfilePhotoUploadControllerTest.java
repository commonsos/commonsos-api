package commonsos.controller.auth;

import commonsos.domain.auth.ImageService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Session;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfilePhotoUploadControllerTest {

  @InjectMocks ProfilePhotoUploadController controller;
  @Mock UserService userService;
  @Mock ImageService imageService;
  @Mock User user;
  @Mock Request request;
  @Mock Session session;

  @Test
  public void handle() {
    when(request.session()).thenReturn(session);
    when(request.body()).thenReturn("data:image/png;base64,QUJD");
    when(imageService.uploadImage("ABC".getBytes())).thenReturn("/url");

    controller.handle(user, request, null);

    verify(userService).setAvatar(user, "/url");
    verify(session).attribute("user", user);
  }
}