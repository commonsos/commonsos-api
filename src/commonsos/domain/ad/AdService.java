package commonsos.domain.ad;

import commonsos.BadRequestException;
import commonsos.ForbiddenException;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static java.time.OffsetDateTime.now;
import static java.util.stream.Collectors.toList;

@Singleton
public class AdService {
  @Inject AdRepository repository;
  @Inject AgreementService agreementService;
  @Inject UserService userService;

  public void create(User user, Ad ad) {
    ad.setCreatedBy(user.getId());
    ad.setCreatedAt(now());
    repository.create(ad);
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
      .setTitle(ad.getTitle())
      .setDescription(ad.getDescription())
      .setPoints(ad.getPoints())
      .setLocation(ad.getLocation())
      .setAcceptable(isAcceptable(ad, user))
      .setCreatedAt(ad.getCreatedAt())
      .setPhotoUrl(ad.getPhotoUrl());
  }

  boolean isAcceptable(Ad ad, User user) {
    return !ad.getCreatedBy().equals(user.getId());
  }

  public Ad accept(User user, String id) {
    Ad ad = repository.find(id).orElseThrow(RuntimeException::new);
    if (!isAcceptable(ad, user)) throw new ForbiddenException();
    repository.save(ad);
    agreementService.create(user, ad);
    return ad;
  }

  public AdView ad(User user, String id) {
    return view(repository.find(id).orElseThrow(BadRequestException::new), user);
  }
}
