package commonsos.domain.community;

import commonsos.BadRequestException;
import commonsos.controller.community.CommunityView;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Singleton
public class CommunityService {

  @Inject CommunityRepository repository;

  public List<CommunityView> list() {
    return repository.list().stream().map(this::view).collect(toList());
  }

  CommunityView view(Community community) {
    return new CommunityView().setId(community.getId()).setName(community.getName());
  }

  public CommunityView community(Long id) {
    return repository.findById(id).map(this::view).orElseThrow(BadRequestException::new);
  }
}
