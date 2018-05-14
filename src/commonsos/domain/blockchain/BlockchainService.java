package commonsos.domain.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import commonsos.domain.auth.User;
import commonsos.domain.community.Community;
import commonsos.domain.community.CommunityRepository;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Files;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Paths;

import static commonsos.domain.auth.UserService.WALLET_PASSWORD;

@Singleton
@Slf4j
public class BlockchainService {

  public static final BigInteger GAS_LIMIT = new BigInteger("90000");
  public static final BigInteger GAS_PRICE = new BigInteger("18000000000");

  @Inject CommunityRepository communityRepository;
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

  public String createTransaction(User remitter, User beneficiary, BigDecimal amount) {
    Community community = communityRepository.findById(remitter.getCommunityId()).orElseThrow(RuntimeException::new);
    try {
      Credentials remitterCredentials = credentials(remitter.getWallet(), WALLET_PASSWORD);
      String tokenContractAddress = community.getTokenContractId();
      TokenERC20 token = loadToken(remitterCredentials, tokenContractAddress);
      log.info(String.format("Creating transaction from %s to %s amount %.0f contract %s", remitter.getWalletAddress(), beneficiary.getWalletAddress(), amount, tokenContractAddress));
      TransactionReceipt transactionReceipt = token.transfer(beneficiary.getWalletAddress(), amount.toBigInteger()).send();
      log.info(String.format("Transaction done, id  %s", transactionReceipt.getTransactionHash()));
      return transactionReceipt.getTransactionHash();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  TokenERC20 loadToken(Credentials remitterCredentials, String tokenContractAddress) {
    return TokenERC20.load(tokenContractAddress, web3j, remitterCredentials, GAS_PRICE, GAS_LIMIT);
  }
}
