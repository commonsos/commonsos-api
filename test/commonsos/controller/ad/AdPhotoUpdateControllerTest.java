package commonsos.controller.ad;

import commonsos.domain.ad.AdPhotoUpdateCommand;
import commonsos.domain.ad.AdService;
import commonsos.domain.auth.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdPhotoUpdateControllerTest {

  @Mock Request request;
  @Mock AdService service;
  @InjectMocks @Spy AdPhotoUpdateController controller;

  @Test
  public void handle() {
    User user = new User();
    ArgumentCaptor<AdPhotoUpdateCommand> commandArgument = ArgumentCaptor.forClass(AdPhotoUpdateCommand.class);
    when(service.updatePhoto(eq(user), commandArgument.capture())).thenReturn("/url");
    when(request.params("id")).thenReturn("123");
    InputStream image = mock(InputStream.class);
    doReturn(image).when(controller).image(request);

    String result = controller.handle(user, request, null);

    assertThat(commandArgument.getValue().getAdId()).isEqualTo(123);
    assertThat(commandArgument.getValue().getPhoto()).isEqualTo(image);
    assertThat(result).isEqualTo("/url");
  }
}