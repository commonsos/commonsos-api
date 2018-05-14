package commonsos.domain.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Files;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Paths;

@Singleton
public class BlockchainService {

  @Inject ObjectMapper objectMapper;

  public static final BigInteger GAS_PRICE = new BigInteger("18000000000");

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
      return Credentials.create(Wallet.decrypt(password, walletFile));
    }
    catch (Exception e) {
      throw new RuntimeException();
    }
  }

}
