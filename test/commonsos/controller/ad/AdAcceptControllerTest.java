package commonsos.controller.ad;

import commonsos.User;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdAcceptControllerTest {
  @Mock Request request;
  @Mock AdService service;
  User user = new User();
  @InjectMocks AdAcceptController controller = new AdAcceptController();

  @Test
  public void handle() throws Exception {
    when(request.params("id")).thenReturn("adId");
    Ad ad = new Ad();
    when(service.accept(user, "adId")).thenReturn(ad);

    assertThat(controller.handle(user, request, null)).isSameAs(ad);
  }
}