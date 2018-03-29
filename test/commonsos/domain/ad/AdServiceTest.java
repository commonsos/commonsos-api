package commonsos.domain.ad;

import commonsos.domain.agreement.AgreementService;
import commonsos.domain.auth.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.TEN;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdServiceTest {

  @Mock AdRepository repository;
  @Mock AgreementService agreementService;
  @InjectMocks AdService adService;

  @Test
  public void create() {
    Ad ad = new Ad();

    adService.create(new User().setId("user id"), ad);

    verify(repository).create(ad);
    assertEquals("user id", ad.getCreatedBy());
  }

  @Test
  public void accept() {
    Ad ad = new Ad();
    when(repository.find("adId")).thenReturn(Optional.of(ad));
    User user = new User();

    Ad result = adService.accept(user, "adId");

    verify(agreementService).create(user, ad);
    assertThat(result).isSameAs(ad);
    verify(repository).save(ad);
  }

  @Test
  public void all() {
    Ad ad = new Ad();
    AdView view = new AdView();
    User user = new User();
    AdService service = spy(adService);
    when(repository.list()).thenReturn(asList(ad));
    doReturn(view).when(service).view(ad, user);

    List<AdView> result = service.all(user);

    assertThat(result).containsExactly(view);
  }

  @Test
  public void view() {
    Ad ad = new Ad()
      .setPoints(TEN)
      .setLocation("home")
      .setDescription("description")
      .setCreatedBy("worker")
      .setId("11")
      .setTitle("title");

    AdView view = adService.view(ad, new User().setId("worker"));

    assertThat(view.getCreatedBy()).isEqualTo("worker");
    assertThat(view.getDescription()).isEqualTo("description");
    assertThat(view.getId()).isEqualTo("11");
    assertThat(view.getLocation()).isEqualTo("home");
    assertThat(view.getPoints()).isEqualTo(TEN);
    assertThat(view.getTitle()).isEqualTo("title");
    assertThat(view.isAcceptable()).isEqualTo(false);
  }

  @Test
  public void isAcceptable() {
    Ad ad = new Ad().setCreatedBy("worker");

    assertThat(adService.isAcceptable(ad, new User().setId("worker"))).isFalse();
    assertThat(adService.isAcceptable(ad, new User().setId("stranger"))).isTrue();
  }
}