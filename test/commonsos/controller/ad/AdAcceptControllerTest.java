package commonsos.controller.ad;

import commonsos.domain.ad.AdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdAcceptControllerTest {
  @Mock Request request;
  @Mock AdService service;
  @InjectMocks AdAcceptController controller = new AdAcceptController();

  @Test
  public void handle() throws Exception {
    when(request.headers("X-UserId")).thenReturn("userId");
    when(request.params("id")).thenReturn("adId");

    controller.handle(request, null);

    verify(service).accept("userId", "adId");
  }
}