package commonsos.controller.auth;

import commonsos.domain.auth.User;
import commonsos.domain.blockchain.BlockchainService;
import org.junit.Test;

import java.math.BigInteger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DelegateWalletTaskTest {

  BlockchainService blockchainService = mock(BlockchainService.class);

  @Test
  public void run() {
    User delegate = new User().setWalletAddress("community wallet");
    User walletOwner = new User().setWalletAddress("member wallet");
    DelegateWalletTask task = new DelegateWalletTask(walletOwner, delegate);
    task.blockchainService = blockchainService;

    task.run();

    verify(blockchainService).transferEther(delegate, "member wallet", new BigInteger("16200000000000000"));
    verify(blockchainService).delegateTokenTransferRight(walletOwner, delegate);
  }
}