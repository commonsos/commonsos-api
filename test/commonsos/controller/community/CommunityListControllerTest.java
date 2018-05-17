package commonsos.controller.community;

import commonsos.domain.community.CommunityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommunityListControllerTest {

  @InjectMocks CommunityListController controller;
  @Mock CommunityService service;

  @Test
  public void handle() {
    List<CommunityView> communities = new ArrayList<>();
    when(service.list()).thenReturn(communities);

    List<CommunityView> result = controller.handle(mock(Request.class), mock(Response.class));

    assertThat(result).isSameAs(communities);
  }
}