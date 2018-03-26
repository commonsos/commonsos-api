package commonsos.controller.reward;

import commonsos.BadRequestException;
import commonsos.GsonProvider;
import commonsos.domain.reward.Transaction;
import commonsos.domain.reward.TransactionService;
import commonsos.domain.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;
import spark.Response;

import static org.assertj.core.api.Assertions.assertThat;
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
    User user = new User();
    Transaction transaction = new Transaction();
    when(request.body()).thenReturn(json("{'code': '12345'}"));
    when(service.claim(user, "12345")).thenReturn(transaction);

    Transaction result = (Transaction)controller.handle(user, request, mock(Response.class));

    assertThat(result).isEqualTo(transaction);
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