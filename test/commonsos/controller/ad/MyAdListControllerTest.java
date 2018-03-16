package commonsos.controller.ad;

import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import java.util.ArrayList;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MyAdListControllerTest {

  @Mock AdService service;
  @Mock Request request;
  @InjectMocks MyAdListController controller;

  @Test
  public void handle() throws Exception {
    when(request.headers("X-UserId")).thenReturn("userId");
    ArrayList<Ad> ads = new ArrayList<>();
    when(service.acceptedBy("userId")).thenReturn(ads);

    assertSame(ads, controller.handle(request, null));
  }
}