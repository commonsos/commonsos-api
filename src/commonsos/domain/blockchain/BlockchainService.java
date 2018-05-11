package commonsos.domain.blockchain;

import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Files;

import javax.inject.Singleton;
import java.io.File;
import java.nio.file.Paths;

@Singleton
public class BlockchainService {

  public String createWallet() {
    File filePath = null;
    try {
      File tmp = tempDir();
      String fileName = WalletUtils.generateFullNewWalletFile("secret", tmp);

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
}
