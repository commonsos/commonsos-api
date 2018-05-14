package commonsos.domain.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;

import static java.math.BigInteger.ONE;

public class TokenERC20Deployer {

  public static final BigInteger INITIAL_TOKEN_AMOUNT = new BigInteger("2").pow(256).subtract(ONE);
  public static final BigInteger TOKEN_DEPLOYMENT_GAS_LIMIT = new BigInteger("262568");

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Please specify administrator wallet file");
      return;
    }

    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override protected void configure() {
        bind(Web3j.class).toInstance(Web3j.build(new HttpService("http://localhost:8545/")));
        bind(ObjectMapper.class).toInstance(new ObjectMapper());
      }
    });

    BlockchainService blockchainService = injector.getInstance(BlockchainService.class);

    System.out.print("Please enter wallet password: ");
    String password = new Scanner(System.in).nextLine();
    System.out.print("Please enter token symbol: ");
    String tokenSymbol = new Scanner(System.in).nextLine();
    System.out.print("Please enter token name: ");
    String tokenName = new Scanner(System.in).nextLine();
    Credentials owner = WalletUtils.loadCredentials(password, new File(args[0]));
    System.out.println("Deploying..");

    String tokenContractAddress = blockchainService.deploy(owner, tokenSymbol, tokenName);
    System.out.println("Contract deployed, address = " + tokenContractAddress);
    System.out.println("\nDo not loose contract address!");
  }
}
