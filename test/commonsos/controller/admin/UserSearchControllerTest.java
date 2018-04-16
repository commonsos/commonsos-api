package commonsos.controller.admin;

import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.auth.UserView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserSearchControllerTest {
  @Mock UserService service;
  @InjectMocks UserSearchController controller;

  @Test
  public void handle() {
    Request request = mock(Request.class);
    when(request.queryParams("q")).thenReturn("john doe");
    ArrayList<UserView> users = new ArrayList<>();
    User user = new User();
    when(service.searchUsers(user, "john doe")).thenReturn(users);

    List<UserView> result = controller.handle(user, request, null);

    assertThat(result).isEqualTo(users);
  }
}