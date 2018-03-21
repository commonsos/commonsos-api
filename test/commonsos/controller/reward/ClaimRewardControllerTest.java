package commonsos.controller.reward;

import commonsos.BadRequestException;
import commonsos.GsonProvider;
import commonsos.User;
import commonsos.domain.reward.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Response;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClaimRewardControllerTest {

  @Mock TransactionService service;
  @Mock Request request;
  @InjectMocks ClaimRewardController controller = new ClaimRewardController();

  @Before
  public void setUp() {
    controller.gson = new GsonProvider().get();
  }

  @Test
  public void claim() {
    when(request.body()).thenReturn(json("{'code': '12345'}"));

    User user = new User();
    controller.handle(user, request, mock(Response.class));

    verify(service).claim(user, "12345");
  }

  @Test(expected = BadRequestException.class)
  public void claim_noCode() {
    when(request.body()).thenReturn("");

    User user = new User();
    controller.handle(user, request, mock(Response.class));

    verify(service, never()).claim(user, "12345");
  }

  private String json(String notReallyJson) {
    return notReallyJson.replace("'", "\"");
  }
}