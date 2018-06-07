package commonsos.domain.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import commonsos.DisplayableException;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.community.Community;
import commonsos.domain.community.CommunityRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.Callable;

import static commonsos.TestId.id;
import static commonsos.domain.auth.UserService.WALLET_PASSWORD;
import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BlockchainServiceTest {

  @Rule public TemporaryFolder tempDirectory = new TemporaryFolder();
  @Mock CommunityRepository communityRepository;
  @Mock UserService userService;
  @Mock(answer = RETURNS_DEEP_STUBS) Web3j web3j;
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
  public void transferTokens_asAdmin() throws Exception {
    User remitter = new User().setAdmin(true).setCommunityId(id("community")).setWallet("remitter wallet");
    User beneficiary = new User().setWalletAddress("beneficiary address");

    Community community = new Community().setTokenContractAddress("contract address");
    when(communityRepository.findById(remitter.getCommunityId())).thenReturn(Optional.of(community));

    EthSendTransaction response = mock(EthSendTransaction.class);
    when(response.getTransactionHash()).thenReturn("transaction hash");

    Credentials credentials = mock(Credentials.class);
    doReturn(credentials).when(service).credentials("remitter wallet", WALLET_PASSWORD);
    doReturn(response).when(service).contractTransfer("contract address", credentials, "beneficiary address", new BigInteger("10000000000000000000"));


    String result = service.transferTokens(remitter, beneficiary, TEN);


    assertThat(result).isEqualTo("transaction hash");
  }

  @Test
  public void signAndSend() throws IOException {
    Credentials credentials = mock(Credentials.class);
    RawTransaction rawTransaction = mock(RawTransaction.class);
    doReturn("signed message").when(service).signMessage(credentials, rawTransaction);
    EthSendTransaction response = mock(EthSendTransaction.class);
    when(response.hasError()).thenReturn(false);
    when(web3j.ethSendRawTransaction("signed message").send()).thenReturn(response);

    EthSendTransaction result = service.signAndSend(credentials, rawTransaction);

    assertThat(result).isSameAs(response);
  }

  @Test
  public void signAndSend_throwsExceptionInCaseOfReponseError() throws IOException {
    Credentials credentials = mock(Credentials.class);
    RawTransaction rawTransaction = mock(RawTransaction.class);
    doReturn("signed message").when(service).signMessage(credentials, rawTransaction);
    EthSendTransaction response = mock(EthSendTransaction.class, Mockito.RETURNS_DEEP_STUBS);
    when(response.hasError()).thenReturn(true);
    when(response.getError().getMessage()).thenReturn("blockchain error");
    when(web3j.ethSendRawTransaction("signed message").send()).thenReturn(response);

    RuntimeException thrown = catchThrowableOfType(() -> {
      service.signAndSend(credentials, rawTransaction);
    }, RuntimeException.class);

    assertThat(thrown).hasMessage("blockchain error");
  }

  @Test
  public void transferTokens_asRegularUser() {
    User remitter = new User().setCommunityId(id("community")).setWalletAddress("remitter address");
    User beneficiary = new User().setWalletAddress("beneficiary address");

    Community community = new Community().setTokenContractAddress("contract address");
    when(communityRepository.findById(remitter.getCommunityId())).thenReturn(Optional.of(community));

    User walletUser = new User().setWalletAddress("admin wallet address").setWallet("admin wallet");
    when(userService.walletUser(community)).thenReturn(walletUser);

    EthSendTransaction response = mock(EthSendTransaction.class);
    when(response.hasError()).thenReturn(false);
    when(response.getTransactionHash()).thenReturn("transaction hash");

    Credentials credentials = mock(Credentials.class);
    doReturn(credentials).when(service).credentials("admin wallet", WALLET_PASSWORD);
    doReturn(response).when(service).contractTransferFrom(credentials, "contract address", "remitter address", "beneficiary address", new BigInteger("10000000000000000000"));


    String result = service.transferTokens(remitter, beneficiary, TEN);


    assertThat(result).isEqualTo("transaction hash");
  }

  @Test
  public void transferTokens_asRegularUser_fails() {
    User remitter = new User().setCommunityId(id("community")).setWalletAddress("remitter address");
    User beneficiary = new User().setWalletAddress("beneficiary address");

    Community community = new Community().setTokenContractAddress("contract address");
    when(communityRepository.findById(remitter.getCommunityId())).thenReturn(Optional.of(community));

    User walletUser = new User().setWalletAddress("admin wallet address").setWallet("admin wallet");
    when(userService.walletUser(community)).thenReturn(walletUser);

    EthSendTransaction response = mock(EthSendTransaction.class, Mockito.RETURNS_DEEP_STUBS);
    when(response.hasError()).thenReturn(true);
    when(response.getError().getMessage()).thenReturn("blockchain error");

    Credentials credentials = mock(Credentials.class);
    doReturn(credentials).when(service).credentials("admin wallet", WALLET_PASSWORD);
    doReturn(response).when(service).contractTransferFrom(any(), any(), any(), any(), any());


    RuntimeException thrown = catchThrowableOfType(
      ()-> service.transferTokens(remitter, beneficiary, TEN),
      RuntimeException.class);
    assertThat(thrown).hasMessage("Error processing transaction request: blockchain error");
  }

  @Test
  public void userCommunityToken() {
    User user = new User().setCommunityId(id("community")).setWallet("wallet");
    when(communityRepository.findById(id("community"))).thenReturn(Optional.of(new Community().setTokenContractAddress("contract address")));

    Credentials remitterCredentials = mock(Credentials.class);
    doReturn(remitterCredentials).when(service).credentials("wallet", WALLET_PASSWORD);

    TokenERC20 token = mock(TokenERC20.class, RETURNS_DEEP_STUBS);
    doReturn(token).when(service).loadToken(remitterCredentials, "contract address");

    TokenERC20 result = service.userCommunityToken(user);

    assertThat(result).isEqualTo(token);
  }

  @Test
  public void userCommunityTokenAsAdmin() {
    User user = new User().setCommunityId(id("community")).setWallet("wallet");
    Community community = new Community().setTokenContractAddress("contract address");
    when(communityRepository.findById(id("community"))).thenReturn(Optional.of(community));
    User admin = new User().setWallet("wallet");
    when(userService.walletUser(community)).thenReturn(admin);

    Credentials remitterCredentials = mock(Credentials.class);
    doReturn(remitterCredentials).when(service).credentials("wallet", WALLET_PASSWORD);

    TokenERC20 token = mock(TokenERC20.class, RETURNS_DEEP_STUBS);
    doReturn(token).when(service).loadToken(remitterCredentials, "contract address");


    TokenERC20 result = service.userCommunityTokenAsAdmin(user);


    assertThat(result).isEqualTo(token);
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
    when(communityRepository.findById(id("community"))).thenReturn(Optional.of(new Community().setTokenContractAddress("contract address")));

    TokenERC20 token = mock(TokenERC20.class, RETURNS_DEEP_STUBS);
    doReturn(token).when(service).loadTokenReadOnly("wallet address", "contract address");
    when(token.balanceOf("wallet address").send()).thenReturn(new BigInteger("10000000000000000000"));


    BigDecimal result = service.tokenBalance(user);


    assertThat(result).isEqualByComparingTo(TEN);
  }

  @Test
  public void handleBlockchainException() {
    Callable<String> action = () -> "result";

    String result = service.handleBlockchainException(action);

    assertThat(result).isEqualTo("result");
  }

  @Test(expected = DisplayableException.class)
  public void handleBlockchainException_handlesInsufficientEtherError() {
    Callable<Void> failingAction = () -> {
      if (true) throw new RuntimeException("insufficient funds for gas");
      return null;
    };

    service.handleBlockchainException(failingAction);
  }

  @Test(expected = RuntimeException.class)
  public void handleBlockchainException_passesThroughRandomError() {
    Callable<Void> failingAction = () -> {
      if (true) throw new RuntimeException("bad luck");
      return null;
    };

    service.handleBlockchainException(failingAction);
  }
}