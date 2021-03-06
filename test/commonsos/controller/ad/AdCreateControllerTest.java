package commonsos.controller.ad;

import com.google.gson.Gson;
import commonsos.domain.ad.AdCreateCommand;
import commonsos.domain.ad.AdService;
import commonsos.domain.ad.AdType;
import commonsos.domain.ad.AdView;
import commonsos.domain.auth.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdCreateControllerTest {

  @Mock Request request;
  @Mock AdService service;
  @InjectMocks AdCreateController controller;

  @Before
  public void setUp() throws Exception {
    controller.gson = new Gson();
  }

  @Test
  public void handle() throws Exception {
    when(request.body()).thenReturn("{\"title\": \"title\", \"description\": \"description\", \"amount\": \"123.456\", \"location\": \"location\", \"type\": \"GIVE\"}");
    User user = new User();
    ArgumentCaptor<AdCreateCommand> captor = ArgumentCaptor.forClass(AdCreateCommand.class);
    AdView adView = new AdView();
    when(service.create(eq(user), captor.capture())).thenReturn(adView);

    AdView result = controller.handle(user, request, null);

    assertThat(result).isEqualTo(adView);
    AdCreateCommand ad = captor.getValue();
    assertEquals("title", ad.getTitle());
    assertEquals("description", ad.getDescription());
    assertEquals(new BigDecimal("123.456"), ad.getAmount());
    assertEquals("location", ad.getLocation());
    assertEquals(AdType.GIVE, ad.getType());
  }
}