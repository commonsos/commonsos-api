package commonsos.controller.ad;

import com.google.gson.Gson;
import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
    when(request.body()).thenReturn("{\"title\": \"title\", \"description\": \"description\", \"points\": \"123.456\", \"location\": \"location\"}");
    User user = new User().setId("user id");

    controller.handle(user, request, null);

    ArgumentCaptor<Ad> captor = ArgumentCaptor.forClass(Ad.class);
    verify(service).create(eq(user), captor.capture());
    Ad ad = captor.getValue();
    assertEquals("title", ad.getTitle());
    assertEquals("description", ad.getDescription());
    assertEquals(new BigDecimal("123.456"), ad.getPoints());
    assertEquals("location", ad.getLocation());
  }
}