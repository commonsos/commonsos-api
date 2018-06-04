package commonsos.controller.auth;

import commonsos.domain.auth.ImageService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProfilePhotoUploadControllerTest {

  @InjectMocks ProfilePhotoUploadController controller;
  @Mock UserService userService;
  @Mock ImageService imageService;
  @Mock User user;
  @Mock Request request;

  @Test
  public void handle() {
    when(request.body()).thenReturn("data:image/png;base64,QUJD");
    ArgumentCaptor<ByteArrayInputStream> streamArgument = ArgumentCaptor.forClass(ByteArrayInputStream.class);
    when(imageService.upload(streamArgument.capture())).thenReturn("/url");

    controller.handle(user, request, null);

    assertThat(streamArgument.getValue()).hasSameContentAs(new ByteArrayInputStream("ABC".getBytes()));
    verify(userService).setAvatar(user, "/url");
  }
}