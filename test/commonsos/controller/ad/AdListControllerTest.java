package commonsos.controller.ad;

import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdService;
import commonsos.domain.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdListControllerTest {

  @Mock AdService service;
  @InjectMocks AdListController controller;

  @Test
  public void handle() throws Exception {
    ArrayList<Ad> ads = new ArrayList<>();
    when(service.all()).thenReturn(ads);

    assertSame(ads, controller.handle(new User(), null, null));
  }
}