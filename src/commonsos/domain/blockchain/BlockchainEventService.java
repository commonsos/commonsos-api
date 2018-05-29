package commonsos.domain.blockchain;

import commonsos.EntityManagerService;
import commonsos.domain.transaction.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class BlockchainEventService {

  @Inject private Web3j web3j;
  @Inject private TransactionService transactionService;
  @Inject private EntityManagerService entityManagerService;

  public void listenEvents() {
    web3j.transactionObservable().subscribe(tx -> {
      log.info(String.format("New transaction event received: hash=%s, from=%s, to=%s, gas=%d ", tx.getHash(), tx.getFrom(), tx.getTo(), tx.getGas()));

      entityManagerService.runInTransaction(() -> transactionService.markTransactionCompleted(tx.getHash()));
    });
  }
}
