package commonsos.domain.community;

import commonsos.BadRequestException;
import commonsos.controller.community.CommunityView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommunityServiceTest {

  @InjectMocks @Spy CommunityService service;
  @Mock CommunityRepository repository;

  @Test
  public void list() {
    Community community = new Community();
    Community community2 = new Community();
    when(repository.list()).thenReturn(asList(community, community2));
    CommunityView view = mock(CommunityView.class);
    CommunityView view2 = mock(CommunityView.class);
    doReturn(view).when(service).view(community);
    doReturn(view2).when(service).view(community2);

    List<CommunityView> result = service.list();

    assertThat(result).containsExactly(view, view2);
  }

  @Test
  public void view() {
    Community community = new Community().setId(1L).setName("name");

    CommunityView view = service.view(community);

    assertThat(view.getId()).isEqualTo(1L);
    assertThat(view.getName()).isEqualTo("name");
  }

  @Test
  public void communityById() {
    Community community = new Community();
    when(repository.findById(123L)).thenReturn(Optional.of(community));
    CommunityView view = new CommunityView();
    when(service.view(community)).thenReturn(view);

    CommunityView result = service.community(123L);

    assertThat(result).isEqualTo(view);
  }

  @Test(expected = BadRequestException.class)
  public void communityById_notFound() {
    when(repository.findById(123L)).thenReturn(Optional.empty());

    service.community(123L);
  }
}