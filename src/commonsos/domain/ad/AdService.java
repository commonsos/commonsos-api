package commonsos.domain.ad;

import commonsos.BadRequestException;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static commonsos.domain.ad.AdType.GIVE;
import static commonsos.domain.ad.AdType.WANT;
import static java.math.BigDecimal.ZERO;
import static java.time.OffsetDateTime.now;
import static java.util.stream.Collectors.toList;

@Singleton
public class AdService {
  @Inject AdRepository repository;
  @Inject UserService userService;

  public Ad create(User user, AdCreateCommand command) {
    Ad ad = new Ad()
      .setCreatedBy(user.getId())
      .setCreatedAt(now())
      .setType(command.getType())
      .setTitle(command.getTitle())
      .setDescription(command.getDescription())
      .setLocation(command.getLocation())
      .setPoints(command.getAmount())
      .setPhotoUrl(command.getPhotoUrl());

    return repository.create(ad);
  }

  public List<AdView> all(User user) {
    return repository.list().stream().map(ad -> view(ad, user)).collect(toList());
  }

  public List<AdView> adsByOwner(User user) {
    return repository.list().stream().filter(a -> a.getCreatedBy().equals(user.getId())).map(ad -> view(ad, user)).collect(toList());
  }

  protected AdView view(Ad ad, User user) {
    return new AdView()
      .setId(ad.getId())
      .setCreatedBy(userService.view(ad.getCreatedBy()))
      .setType(ad.getType())
      .setTitle(ad.getTitle())
      .setDescription(ad.getDescription())
      .setPoints(ad.getPoints())
      .setLocation(ad.getLocation())
      .setOwn(isOwnAd(user, ad))
      .setPayable(isPayableByUser(user, ad))
      .setCreatedAt(ad.getCreatedAt())
      .setPhotoUrl(ad.getPhotoUrl());
  }

  public AdView view(User user, String adId) {
    return view(ad(adId), user);
  }

  boolean isOwnAd(User user, Ad ad) {
    return ad.getCreatedBy().equals(user.getId());
  }

  public boolean isPayableByUser(User user, Ad ad) {
    if (ZERO.compareTo(ad.getPoints()) >= 0) return false;
    if (isOwnAd(user, ad) && WANT == ad.getType()) return true;
    if (!isOwnAd(user, ad) && GIVE == ad.getType()) return true;
    return false;
  }

  public Ad ad(String id) {
    return repository.find(id).orElseThrow(BadRequestException::new);
  }
}
