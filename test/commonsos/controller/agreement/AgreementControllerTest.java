package commonsos.controller.agreement;

import commonsos.domain.agreement.AgreementService;
import commonsos.domain.agreement.AgreementViewModel;
import commonsos.domain.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgreementControllerTest {

  @Mock User user;
  @Mock AgreementService service;
  @Mock Request request;
  @InjectMocks AgreementController controller;

  @Test
  public void handle() {
    AgreementViewModel result = new AgreementViewModel();
    when(service.details(user,  "agreement id")).thenReturn(result);
    when(request.params("id")).thenReturn("agreement id");

    assertThat(controller.handle(user, request, null)).isEqualTo(result);
  }
}