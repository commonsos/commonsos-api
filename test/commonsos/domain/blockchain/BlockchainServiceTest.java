package commonsos.domain.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class BlockchainServiceTest {

  @Rule public TemporaryFolder tempDirectory = new TemporaryFolder();
  BlockchainService service = spy(new BlockchainService());

  @Test
  public void createWallet() throws IOException, CipherException {
    doReturn(tempDirectory.getRoot()).when(service).tempDir();

    String wallet = service.createWallet();

    assertThat(credentials(wallet)).isNotNull();
    assertThat(tempDirectory.getRoot().listFiles()).isEmpty();
  }

  private Credentials credentials(String wallet) throws CipherException, IOException {
    return Credentials.create(Wallet.decrypt("secret", new ObjectMapper().readValue(wallet, WalletFile.class)));
  }
}