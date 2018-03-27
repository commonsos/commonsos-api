package commonsos.controller.transaction;

import commonsos.domain.auth.User;
import commonsos.domain.transaction.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BalanceControllerTest {

  @Mock TransactionService service;
  @InjectMocks BalanceController controller;

  @Test
  public void handle() {
    User user = new User();
    when(service.balance(user)).thenReturn(TEN);

    assertThat(controller.handle(user, null, null)).isEqualTo(TEN);
  }
}