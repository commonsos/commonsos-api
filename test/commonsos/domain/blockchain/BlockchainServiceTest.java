package commonsos.domain.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import commonsos.domain.auth.User;
import commonsos.domain.community.Community;
import commonsos.domain.community.CommunityRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static commonsos.TestId.id;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BlockchainServiceTest {

  @Rule public TemporaryFolder tempDirectory = new TemporaryFolder();
  @Mock CommunityRepository communityRepository;
  @InjectMocks @Spy BlockchainService service;

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

  @Test
  public void transferTokens() throws Exception {
    User remitter = new User().setCommunityId(id("community")).setWallet("remitter wallet");
    User beneficiary = new User().setWalletAddress("beneficiary wallet address");

    when(communityRepository.findById(id("community"))).thenReturn(Optional.of(new Community().setTokenContractId("contract id")));

    Credentials remitterCredentials = mock(Credentials.class);
    doReturn(remitterCredentials).when(service).credentials("remitter wallet", "test");

    TransactionReceipt transactionReceipt = new TransactionReceipt();
    transactionReceipt.setTransactionHash("new transaction id");

    TokenERC20 token = mock(TokenERC20.class, RETURNS_DEEP_STUBS);
    doReturn(token).when(service).loadTokenReadOnly(remitterCredentials, "contract id");
    when(token.transfer(any(), any()).send()).thenReturn(transactionReceipt);


    String result = service.transferTokens(remitter, beneficiary, BigDecimal.TEN);


    assertThat(result).isEqualTo("new transaction id");
    verify(token).transfer("beneficiary wallet address", new BigInteger("10000000000000000000"));
  }

  @Test
  public void createToken() throws Exception {
    User owner = new User().setWallet("wallet");
    Credentials credentials = mock(Credentials.class);
    doReturn(credentials).when(service).credentials("wallet", "test");
    doReturn("0x543210").when(service).deploy(credentials, "COM", "COM token");

    String tokenAddress = service.createToken(owner, "COM", "COM token");

    assertThat(tokenAddress).isEqualTo("0x543210");
  }

  @Test
  public void tokenBalance() throws Exception {
    User user = new User().setWalletAddress("wallet address").setCommunityId(id("community"));
    when(communityRepository.findById(id("community"))).thenReturn(Optional.of(new Community().setTokenContractId("contract id")));

    TokenERC20 token = mock(TokenERC20.class, RETURNS_DEEP_STUBS);
    doReturn(token).when(service).loadTokenReadOnly("wallet address", "contract id");
    when(token.balanceOf("wallet address").send()).thenReturn(new BigInteger("10000000000000000000"));


    BigDecimal result = service.tokenBalance(user);


    assertThat(result).isEqualByComparingTo(BigDecimal.TEN);
  }
}