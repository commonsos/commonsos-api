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

    String wallet = service.createWallet("secret");

    assertThat(credentials(wallet)).isNotNull();
    assertThat(tempDirectory.getRoot().listFiles()).isEmpty();
  }

  private Credentials credentials(String wallet) throws CipherException, IOException {
    return Credentials.create(Wallet.decrypt("secret", new ObjectMapper().readValue(wallet, WalletFile.class)));
  }

  @Test
  public void credentials() {
    service.objectMapper = new ObjectMapper();
    String wallet = "{\"address\":\"116ca1e1cc960a033a613f442e3c1bfc91841521\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"a5433cfd68ab9c72aa0e6174a03b4c8093e71db9b6e6ac941d681a34119e2eb9\",\"cipherparams\":{\"iv\":\"ffc2a2393dfb60914ef723a4bb441297\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"e4a8e82817fddd7dcfa8fa87db947aedd6ab5d13a6d5522cf725c9dfbff855a9\"},\"mac\":\"ab674a8909c6e6a26ff7176f7f8a6708a2a621645341a7de7691ce9672893f8a\"},\"id\":\"98f240c5-aa47-42fd-9fab-6ba29722cb15\",\"version\":3}";

    Credentials credentials = service.credentials(wallet, "test");

    assertThat(credentials.getAddress()).isEqualTo("0x116ca1e1cc960a033a613f442e3c1bfc91841521");
  }
}