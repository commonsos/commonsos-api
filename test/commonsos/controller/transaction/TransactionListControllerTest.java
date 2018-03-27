package commonsos.controller.transaction;

import commonsos.domain.auth.User;
import commonsos.domain.reward.Transaction;
import commonsos.domain.reward.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionListControllerTest {

  @Mock TransactionService service;
  @InjectMocks TransactionListController controller;

  @Test
  public void handle() {
    ArrayList<Transaction> transactions = new ArrayList<>();
    User user = new User();
    when(service.transactions(user)).thenReturn(transactions);
    assertThat(controller.handle(user, null, null)).isSameAs(transactions);
  }
}