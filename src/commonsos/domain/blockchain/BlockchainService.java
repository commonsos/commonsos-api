package commonsos.domain.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import commonsos.DisplayableException;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.community.Community;
import commonsos.domain.community.CommunityRepository;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Files;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;

import static commonsos.domain.auth.UserService.WALLET_PASSWORD;
import static commonsos.domain.blockchain.TokenERC20.FUNC_TRANSFERFROM;
import static java.lang.String.format;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TEN;
import static org.web3j.utils.Convert.Unit.WEI;

@Singleton
@Slf4j
public class BlockchainService {

  public static final BigInteger TOKEN_TRANSFER_GAS_LIMIT = new BigInteger("90000");
  public static final BigInteger TOKEN_TRANSFER_FROM_GAS_LIMIT = new BigInteger("339000");
  public static final BigInteger TOKEN_DEPLOYMENT_GAS_LIMIT = new BigInteger("2625681");
  public static final BigInteger GAS_PRICE = new BigInteger("18000000000");

  private static final int NUMBER_OF_DECIMALS = 18;
  private static final BigInteger MAX_UINT_256 = new BigInteger("2").pow(256);
  private static final BigInteger INITIAL_TOKEN_AMOUNT = MAX_UINT_256.divide(TEN.pow(NUMBER_OF_DECIMALS)).subtract(ONE);

  @Inject CommunityRepository communityRepository;
  @Inject UserService userService;
  @Inject ObjectMapper objectMapper;
  @Inject Web3j web3j;

  public String createWallet(String password) {
    File filePath = null;
    try {
      File tmp = tempDir();
      String fileName = WalletUtils.generateFullNewWalletFile(password, tmp);

      filePath = Paths.get(tmp.getAbsolutePath(), fileName).toFile();
      return Files.readString(filePath);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      if (filePath != null) filePath.delete();
    }
  }

  File tempDir() {
    return new File(System.getProperty("java.io.tmpdir"));
  }

  public Credentials credentials(String wallet, String password) {
    try {
      WalletFile walletFile = objectMapper.readValue(wallet, WalletFile.class);
      ECKeyPair keyPair = Wallet.decrypt(password, walletFile);
      Credentials credentials = Credentials.create(keyPair);
      return credentials;
    }
    catch (Exception e) {
      throw new RuntimeException();
    }
  }

  public String transferTokens(User remitter, User beneficiary, BigDecimal amount) {
    return remitter.isAdmin() ?
      transferTokensAdmin(remitter, beneficiary, amount) :
      transferTokensRegular(remitter, beneficiary, amount);
  }

  private String transferTokensRegular(User remitter, User beneficiary, BigDecimal amount) {
    Community community = communityRepository.findById(remitter.getCommunityId()).orElseThrow(RuntimeException::new);
    User walletUser = userService.walletUser(community);

    log.info(format("Creating token transaction from %s to %s amount %.0f contract %s", remitter.getWalletAddress(), beneficiary.getWalletAddress(), amount, community.getTokenContractId()));

    EthSendTransaction response = contractTransferFrom(
      credentials(walletUser.getWallet(), WALLET_PASSWORD),
      community.getTokenContractId(),
      remitter.getWalletAddress(),
      beneficiary.getWalletAddress(),
      toTokensWithoutDecimals(amount)
    );

    if (response.hasError())
      throw new RuntimeException("Error processing transaction request: " + response.getError().getMessage());

    log.info(format("Token transaction sent, hash %s", response.getTransactionHash()));
    return response.getTransactionHash();
  }

  EthSendTransaction contractTransferFrom(Credentials sender, String contractAddress, String from, String to, BigInteger amount) {
    return handleBlockchainException(() -> {
      BigInteger nonce = web3j.ethGetTransactionCount(sender.getAddress(), DefaultBlockParameterName.LATEST).send().getTransactionCount();
      String encodedFunction = FunctionEncoder.encode(new Function(
        FUNC_TRANSFERFROM,
        Arrays.<Type>asList(
          new org.web3j.abi.datatypes.Address(from),
          new org.web3j.abi.datatypes.Address(to),
          new org.web3j.abi.datatypes.generated.Uint256(amount)),
        Collections.<TypeReference<?>>emptyList()));

      RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, GAS_PRICE, TOKEN_TRANSFER_FROM_GAS_LIMIT, contractAddress, encodedFunction);

      byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, sender);
      return web3j.ethSendRawTransaction("0x" + Hex.toHexString(signedMessage)).send();
    });
  }

  private String transferTokensAdmin(User remitter, User beneficiary, BigDecimal amount) {
    return handleBlockchainException(() -> {
      TokenERC20 token = userCommunityToken(remitter);
      log.info(format("Creating token transaction from %s to %s amount %.0f contract %s", remitter.getWalletAddress(), beneficiary.getWalletAddress(), amount, token.getContractAddress()));
      TransactionReceipt transactionReceipt = token.transfer(beneficiary.getWalletAddress(), toTokensWithoutDecimals(amount)).send();
      log.info(format("Token transaction done, id  %s", transactionReceipt.getTransactionHash()));
      return transactionReceipt.getTransactionHash();
    });
  }

  private BigInteger toTokensWithoutDecimals(BigDecimal amount) {
    return amount.multiply(BigDecimal.TEN.pow(NUMBER_OF_DECIMALS)).toBigIntegerExact();
  }

  private BigDecimal toTokensWithDecimals(BigInteger amount) {
    return new BigDecimal(amount).divide(BigDecimal.TEN.pow(NUMBER_OF_DECIMALS));
  }

  TokenERC20 loadToken(Credentials remitterCredentials, String tokenContractAddress) {
    return TokenERC20.load(tokenContractAddress, web3j, remitterCredentials, GAS_PRICE, TOKEN_TRANSFER_GAS_LIMIT);
  }

  TokenERC20 loadTokenReadOnly(String walletAddress, String tokenContractAddress) {
    return TokenERC20.load(tokenContractAddress, web3j, new ReadonlyTransactionManager(web3j, walletAddress), GAS_PRICE, TOKEN_TRANSFER_GAS_LIMIT);
  }

  public void transferEther(User remitter, String beneficiaryWalletAddress, BigInteger amount) {
    transferEther(credentials(remitter.getWallet(), WALLET_PASSWORD), beneficiaryWalletAddress, amount);
  }

  public void transferEther(Credentials remitter, String beneficiaryWalletAddress, BigInteger amount) {
    handleBlockchainException(() -> {
      log.info(format("Creating ether transaction from %s to %s amount %d", remitter.getAddress(), beneficiaryWalletAddress, amount));
      TransactionReceipt transactionReceipt = Transfer.sendFunds(web3j, remitter, beneficiaryWalletAddress, new BigDecimal(amount), WEI).send();
      log.info("Ether transaction complete, id = " + transactionReceipt.getTransactionHash());
      return null;
    });
  }

  <T> T handleBlockchainException(Callable<T> callable) {
    try {
      return callable.call();
    }
    catch (Exception e) {
      if (e.getMessage().contains("insufficient funds for gas"))
        throw new DisplayableException("error.outOfEther");
      throw new RuntimeException(e);
    }
  }

  public String createToken(User owner, String symbol, String name) {
    return handleBlockchainException(() -> {
      Credentials credentials = credentials(owner.getWallet(), "test");
      log.info("Deploying token contract: " + name + " (" + symbol + "), owner: " + owner.getWalletAddress());
      String tokenContractAddress = deploy(credentials, symbol, name);
      log.info("Deploy successful, contract address: " + tokenContractAddress);
      return tokenContractAddress;
    });
  }

  String deploy(Credentials credentials, String symbol, String name) throws Exception {
    TokenERC20 token = TokenERC20.deploy(web3j, credentials, GAS_PRICE, TOKEN_DEPLOYMENT_GAS_LIMIT, INITIAL_TOKEN_AMOUNT, name, symbol).send();
    System.out.println(token.balanceOf(credentials.getAddress()).send() + " " + token.symbol().send());
    return token.getContractAddress();
  }

  public BigInteger etherBalance(String address) {
    try {
      log.info("Ether balance request for: " + address);
      BigInteger balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();
      log.info("Ether balance request complete, balance " + balance.toString());
      return balance;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public BigDecimal tokenBalance(User user) {
    try {
      log.info("Token balance request for: " + user.getWalletAddress());
      Community community = communityRepository.findById(user.getCommunityId()).orElseThrow(RuntimeException::new);
      TokenERC20 token = loadTokenReadOnly(user.getWalletAddress(), community.getTokenContractId());
      BigInteger balance = token.balanceOf(user.getWalletAddress()).send();
      log.info("Token balance request complete, balance " + balance.toString());
      return toTokensWithDecimals(balance);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void delegateUser(User user, User delegated) {
    try {
      TokenERC20 token = userCommunityToken(user);
      TransactionReceipt receipt = token.approve(delegated.getWalletAddress(), INITIAL_TOKEN_AMOUNT).send();
      log.info(format("Wallet %s delegated %s. Gas used %d", user.getWalletAddress(), delegated.getWalletAddress(), receipt.getGasUsed()));
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  TokenERC20 userCommunityToken(User user) {
    Community community = communityRepository.findById(user.getCommunityId()).orElseThrow(RuntimeException::new);
    Credentials credentials = credentials(user.getWallet(), WALLET_PASSWORD);
    return loadToken(credentials, community.getTokenContractId());
  }

  TokenERC20 userCommunityTokenAsAdmin(User user) {
    Community community = communityRepository.findById(user.getCommunityId()).orElseThrow(RuntimeException::new);
    User walletUser = userService.walletUser(community);
    Credentials credentials = credentials(walletUser.getWallet(), WALLET_PASSWORD);
    return loadToken(credentials, community.getTokenContractId());
  }
}
