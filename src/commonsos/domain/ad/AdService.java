package commonsos.domain.ad;

import commonsos.ForbiddenException;
import commonsos.domain.agreement.AgreementService;
import commonsos.domain.auth.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Singleton
public class AdService {
  @Inject AdRepository repository;
  @Inject AgreementService agreementService;

  public void create(User user, Ad ad) {
    ad.setCreatedBy(user.getId());
    repository.create(ad);
  }

  public List<AdView> all(User user) {
    return repository.list().stream().map(ad -> view(ad, user)).collect(toList());
  }

  protected AdView view(Ad ad, User user) {
    return new AdView()
      .setId(ad.getId())
      .setCreatedBy(ad.getCreatedBy())
      .setTitle(ad.getTitle())
      .setDescription(ad.getDescription())
      .setPoints(ad.getPoints())
      .setLocation(ad.getLocation())
      .setAcceptable(isAcceptable(ad, user));
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
}
