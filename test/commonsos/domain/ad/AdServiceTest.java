package commonsos.domain.ad;

import commonsos.BadRequestException;
import commonsos.ForbiddenException;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.TEN;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdServiceTest {

  @Mock AdRepository repository;
  @Mock AgreementService agreementService;
  @Mock UserService userService;
  @InjectMocks @Spy AdService service;

  @Test
  public void create() {
    Ad ad = new Ad();

    service.create(new User().setId("user id"), ad);

    verify(repository).create(ad);
    assertEquals("user id", ad.getCreatedBy());
    assertThat(ad.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
  }

  @Test
  public void accept() {
    Ad ad = new Ad();
    when(repository.find("adId")).thenReturn(Optional.of(ad));
    User user = new User();
    doReturn(true).when(service).isAcceptable(ad, user);

    Ad result = service.accept(user, "adId");

    verify(agreementService).create(user, ad);
    assertThat(result).isSameAs(ad);
    verify(repository).save(ad);
  }

  @Test(expected = ForbiddenException.class)
  public void accept_forbidden() {
    Ad ad = new Ad();
    when(repository.find("adId")).thenReturn(Optional.of(ad));
    User user = new User();
    doReturn(false).when(service).isAcceptable(ad, user);

    service.accept(user, "adId");
  }

  @Test
  public void all() {
    Ad ad = new Ad();
    AdView view = new AdView();
    User user = new User();
    AdService service = spy(this.service);
    when(repository.list()).thenReturn(asList(ad));
    doReturn(view).when(service).view(ad, user);

    List<AdView> result = service.all(user);

    assertThat(result).containsExactly(view);
  }

  @Test
  public void adsByOwner() {
    User user = new User().setId("worker");
    Ad ad = new Ad().setCreatedBy("worker");
    AdView adView = new AdView();
    AdService service = spy(this.service);
    when(repository.list()).thenReturn(asList(ad, new Ad().setCreatedBy("elderly")));
    doReturn(adView).when(service).view(ad, user);

    List<AdView> result = service.adsByOwner(user);

    assertThat(result).containsExactly(adView);
  }

  @Test
  public void view() {
    OffsetDateTime createdAt = now();
    Ad ad = new Ad()
      .setPoints(TEN)
      .setLocation("home")
      .setDescription("description")
      .setCreatedBy("worker")
      .setId("11")
      .setTitle("title")
      .setCreatedAt(createdAt)
      .setPhotoUrl("photo url");
    UserView userView = new UserView();
    when(userService.view("worker")).thenReturn(userView);

    AdView view = service.view(ad, new User().setId("worker"));

    assertThat(view.getCreatedBy()).isEqualTo(userView);
    assertThat(view.getDescription()).isEqualTo("description");
    assertThat(view.getId()).isEqualTo("11");
    assertThat(view.getLocation()).isEqualTo("home");
    assertThat(view.getPoints()).isEqualTo(TEN);
    assertThat(view.getTitle()).isEqualTo("title");
    assertThat(view.isAcceptable()).isEqualTo(false);
    assertThat(view.getCreatedAt()).isEqualTo(createdAt);
    assertThat(view.getPhotoUrl()).isEqualTo("photo url");
  }

  @Test
  public void isAcceptable() {
    Ad ad = new Ad().setCreatedBy("worker");

    assertThat(service.isAcceptable(ad, new User().setId("worker"))).isFalse();
    assertThat(service.isAcceptable(ad, new User().setId("stranger"))).isTrue();
  }

  @Test
  public void ad() {
    Ad ad = new Ad();
    User user = new User();
    when(repository.find("ad id")).thenReturn(Optional.of(ad));
    AdView adView = new AdView();
    doReturn(adView).when(service).view(ad, user);

    AdView result = service.ad(user, "ad id");

    assertThat(result).isEqualTo(adView);
  }

  @Test(expected= BadRequestException.class)
  public void ad_notFound() {
    when(repository.find("ad id")).thenReturn(Optional.empty());

    service.ad(new User(), "ad id");
  }
}