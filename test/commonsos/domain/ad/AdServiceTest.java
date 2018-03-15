package commonsos.domain.ad;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdServiceTest {

  @Mock AdRepository repository;
  @InjectMocks AdService service;

  @Test
  public void create() {
    Ad ad = new Ad();

    service.create("user id", ad);

    verify(repository).create(ad);
    assertEquals("user id", ad.getCreatedBy());
  }

  @Test
  public void list_filters_out_accepted_ads() {
    Ad notAcceptedAd = new Ad();
    when(repository.list()).thenReturn(asList(notAcceptedAd, new Ad().setAcceptedBy("john")));

    List<Ad> result = service.list();

    assertThat(result).containsExactly(notAcceptedAd);
  }

  @Test
  public void accept() {
    Ad ad = new Ad();
    when(repository.find("adId")).thenReturn(Optional.of(ad));

    service.accept("userId", "adId");

    assertEquals("userId", ad.getAcceptedBy());
    verify(repository).save(ad);
  }
}