package commonsos.domain.ad;

import commonsos.BadRequestException;
import commonsos.ForbiddenException;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static commonsos.domain.ad.AdType.GIVE;
import static commonsos.domain.ad.AdType.WANT;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdServiceTest {

  @Mock AdRepository repository;
  @Mock AgreementService agreementService;
  @Mock UserService userService;
  @Captor ArgumentCaptor<Ad> adCaptor;
  @InjectMocks @Spy AdService service;

  @Test
  public void create() {
    AdCreateCommand command = new AdCreateCommand()
      .setTitle("title")
      .setDescription("description")
      .setAmount(TEN)
      .setLocation("location")
      .setType(WANT);

    service.create(new User().setId("user id"), command);

    verify(repository).create(adCaptor.capture());
    Ad ad = adCaptor.getValue();
    assertThat(ad.getCreatedBy()).isEqualTo("user id");
    assertThat(ad.getCreatedAt()).isCloseTo(now(), within(1, SECONDS));
    assertThat(ad.getTitle()).isEqualTo("title");
    assertThat(ad.getDescription()).isEqualTo("description");
    assertThat(ad.getPoints()).isEqualTo(TEN);
    assertThat(ad.getLocation()).isEqualTo("location");
    assertThat(ad.getType()).isEqualTo(WANT);
  }

  @Test
  public void accept() {
    Ad ad = new Ad().setCreatedBy("otherUserId");
    when(repository.find("adId")).thenReturn(Optional.of(ad));
    User user = new User().setId("user");

    Ad result = service.accept(user, "adId");

    verify(agreementService).create(user, ad);
    assertThat(result).isSameAs(ad);
    verify(repository).save(ad);
  }

  @Test(expected = ForbiddenException.class)
  public void accept_forbiddenForOwnAd() {
    Ad ad = new Ad().setCreatedBy("userId");
    when(repository.find("adId")).thenReturn(Optional.of(ad));
    User user = new User().setId("userId");

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
      .setPhotoUrl("photo url")
      .setType(WANT);
    UserView userView = new UserView();
    when(userService.view("worker")).thenReturn(userView);

    AdView view = service.view(ad, new User().setId("worker"));

    assertThat(view.getCreatedBy()).isEqualTo(userView);
    assertThat(view.getDescription()).isEqualTo("description");
    assertThat(view.getId()).isEqualTo("11");
    assertThat(view.getLocation()).isEqualTo("home");
    assertThat(view.getPoints()).isEqualTo(TEN);
    assertThat(view.getTitle()).isEqualTo("title");
    assertThat(view.isOwn()).isEqualTo(true);
    assertThat(view.isPayable()).isEqualTo(true);
    assertThat(view.getCreatedAt()).isEqualTo(createdAt);
    assertThat(view.getPhotoUrl()).isEqualTo("photo url");
    assertThat(view.getType()).isEqualTo(WANT);
  }

  @Test
  public void isOwn() {
    Ad ad = new Ad().setCreatedBy("worker");

    assertThat(service.isOwn(ad, new User().setId("worker"))).isTrue();
    assertThat(service.isOwn(ad, new User().setId("stranger"))).isFalse();
  }

  @Test
  public void isPayable() {
    User me = new User().setId("me");
    User otherUser = new User().setId("other");

    Ad buyAd = new Ad().setCreatedBy("other").setType(AdType.WANT).setPoints(ONE);
    Ad sellAd = new Ad().setCreatedBy("other").setType(GIVE).setPoints(ONE);
    Ad sellAdWithZeroPrice = new Ad().setCreatedBy("other").setType(GIVE).setPoints(ZERO);

    assertThat(service.isPayable(sellAd, me)).isTrue();
    assertThat(service.isPayable(buyAd, otherUser)).isTrue();

    assertThat(service.isPayable(sellAdWithZeroPrice, me)).isFalse();
    assertThat(service.isPayable(buyAd, me)).isFalse();
    assertThat(service.isPayable(sellAd, otherUser)).isFalse();
    assertThat(service.isPayable(buyAd, me)).isFalse();
  }

  @Test
  public void ad() {
    Ad ad = new Ad();
    when(repository.find("ad id")).thenReturn(Optional.of(ad));

    assertThat(service.ad("ad id")).isEqualTo(ad);
  }

  @Test(expected=BadRequestException.class)
  public void ad_notFound() {
    when(repository.find("ad id")).thenReturn(Optional.empty());

    service.ad("ad id");
  }

  @Test
  public void viewById() {
    User user = new User();
    AdView adView = new AdView();
    Ad ad = new Ad();
    doReturn(adView).when(service).view(ad, user);
    doReturn(ad).when(service).ad("ad id");

    AdView result = service.view(user, "ad id");

    assertThat(result).isEqualTo(adView);
  }
}