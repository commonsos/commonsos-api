package commonsos.controller.ad;

import commonsos.domain.ad.AdService;
import commonsos.domain.ad.AdView;
import commonsos.domain.auth.User;
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
    ArrayList<AdView> ads = new ArrayList<>();
    User user = new User();
    when(service.listFor(user)).thenReturn(ads);

    assertSame(ads, controller.handle(user, null, null));
  }
}