package commonsos.domain.ad;

import commonsos.BadRequestException;
import commonsos.ForbiddenException;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static commonsos.domain.ad.AdType.*;
import static java.math.BigDecimal.ZERO;
import static java.time.OffsetDateTime.now;
import static java.util.stream.Collectors.toList;

@Singleton
public class AdService {
  @Inject AdRepository repository;
  @Inject AgreementService agreementService;
  @Inject UserService userService;

  public Ad create(User user, AdCreateCommand command) {
    Ad ad = new Ad()
      .setCreatedBy(user.getId())
      .setCreatedAt(now())
      .setType(command.getType())
      .setTitle(command.getTitle())
      .setDescription(command.getDescription())
      .setLocation(command.getLocation())
      .setPoints(command.getAmount());

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
      .setOwn(isOwn(ad, user))
      .setPayable(isPayable(ad, user))
      .setCreatedAt(ad.getCreatedAt())
      .setPhotoUrl(ad.getPhotoUrl());
  }

  public AdView view(User user, String adId) {
    return view(ad(adId), user);
  }

  boolean isOwn(Ad ad, User user) {
    return ad.getCreatedBy().equals(user.getId());
  }

  public boolean isPayable(Ad ad, User user) {
    if (ZERO.compareTo(ad.getPoints()) >= 0) return false;
    if (isOwn(ad, user) && WANT == ad.getType()) return true;
    if (!isOwn(ad, user) && GIVE == ad.getType()) return true;
    return false;
  }

  public Ad accept(User user, String id) {
    Ad ad = repository.find(id).orElseThrow(RuntimeException::new);
    if (isOwn(ad, user)) throw new ForbiddenException();
    repository.save(ad);
    agreementService.create(user, ad);
    return ad;
  }

  public Ad ad(String id) {
    return repository.find(id).orElseThrow(BadRequestException::new);
  }
}
