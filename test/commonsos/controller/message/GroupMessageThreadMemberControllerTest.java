package commonsos.controller.message;

import commonsos.GsonProvider;
import commonsos.domain.auth.User;
import commonsos.domain.message.AddGroupMemberCommand;
import commonsos.domain.message.MessageService;
import commonsos.domain.message.MessageThreadView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static com.google.common.primitives.Longs.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupMessageThreadMemberControllerTest {

  @InjectMocks GroupMessageThreadMemberController controller;
  @Mock Request request;
  @Mock MessageService service;

  @Before
  public void setGson() {
    controller.gson = new GsonProvider().get();
  }

  @Test
  public void handle() {
    when(request.body()).thenReturn("{\"threadId\": 99, \"memberIds\": [11, 33]}");
    User user = new User();
    MessageThreadView view = new MessageThreadView();
    when(service.groupMember(user, new AddGroupMemberCommand().setThreadId(99L).setMemberIds(asList(11, 33)))).thenReturn(view);

    MessageThreadView result = controller.handle(user, request, null);

    assertThat(result).isSameAs(view);
  }
}