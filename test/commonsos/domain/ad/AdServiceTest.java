package commonsos.domain.ad;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

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
  public void accept() {
    Ad ad = new Ad();
    when(repository.find("adId")).thenReturn(Optional.of(ad));

    Ad result = service.accept("userId", "adId");

    assertThat(result).isSameAs(ad);
    assertThat(ad.getAcceptedBy()).isEqualTo("userId");
    verify(repository).save(ad);
  }
}