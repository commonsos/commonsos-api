package commonsos.domain.ad;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdServiceTest {

  @Mock AdRepository repository;
  @InjectMocks AdService service;

  @Test
  public void create() {
    Ad ad = new Ad();

    service.create(ad);

    verify(repository).create(ad);
  }

  @Test
  public void list() {
    List<Ad> ads = new ArrayList<>();
    when(repository.list()).thenReturn(ads);

    assertSame(ads, service.list());
  }
}