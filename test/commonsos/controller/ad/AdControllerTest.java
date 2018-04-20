package commonsos.controller.ad;

import commonsos.domain.ad.AdService;
import commonsos.domain.ad.AdView;
import commonsos.domain.auth.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdControllerTest {

  @Mock Request request;
  @Mock AdService service;
  @InjectMocks AdController controller;

  @Test
  public void handle() {
    when(request.params("id")).thenReturn("ad id");
    User user = new User();
    AdView adView = new AdView();
    when(service.view(user, "ad id")).thenReturn(adView);

    AdView result = controller.handle(user, request, null);

    assertThat(result).isEqualTo(adView);
  }
}