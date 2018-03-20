package commonsos.controller.ad;

import commonsos.User;
import commonsos.controller.agreement.AgreementListController;
import commonsos.domain.agreement.Agreement;
import commonsos.domain.agreement.AgreementService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import spark.Request;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AgreementListControllerTest {

  @Mock AgreementService service;
  @Mock Request request;
  @InjectMocks AgreementListController controller;

  @Test
  public void handle() {
    ArrayList<Agreement> agreements = new ArrayList<>();
    when(service.list("userId")).thenReturn(agreements);

    assertThat(controller.handle(new User().setId("userId"), request, null)).isSameAs(agreements);
  }
}