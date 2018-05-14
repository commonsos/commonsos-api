package commonsos.domain.blockchain;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;

import static commonsos.domain.blockchain.BlockchainResearch.web3;
import static commonsos.domain.blockchain.BlockchainService.GAS_PRICE;
import static java.math.BigInteger.ONE;

public class TokenERC20Deployer {

  public static final BigInteger INITIAL_TOKEN_AMOUNT = new BigInteger("2").pow(256).subtract(ONE);
  public static final BigInteger DEPLOYMENT_GAS_LIMIT = new BigInteger("262568");

  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Please specify administrator wallet file");
      return;
    }
    System.out.print("Please enter wallet password: ");
    String password = new Scanner(System.in).nextLine();
    System.out.print("Please enter token symbol: ");
    String tokenSymbol = new Scanner(System.in).nextLine();
    System.out.print("Please enter token name: ");
    String tokenName = new Scanner(System.in).nextLine();
    Credentials owner = WalletUtils.loadCredentials(password, new File(args[0]));
    System.out.println("Deploying..");
    TokenERC20 contract = TokenERC20.deploy(web3, owner, GAS_PRICE, DEPLOYMENT_GAS_LIMIT, INITIAL_TOKEN_AMOUNT, tokenName, tokenSymbol).send();
    System.out.println("Contract deployed, address = " + contract.getContractAddress());
    System.out.println("\nDo not loose contract address!");
  }
}
