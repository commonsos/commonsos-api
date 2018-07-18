package commonsos.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import commonsos.EntityManagerService;
import commonsos.Web3jProvider;
import commonsos.domain.auth.AccountCreateCommand;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserRepository;
import commonsos.domain.auth.UserService;
import commonsos.domain.blockchain.BlockchainService;
import commonsos.domain.community.Community;
import commonsos.domain.community.CommunityRepository;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;

import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;

import static commonsos.domain.blockchain.BlockchainService.*;

public class AddCommunity {

  private static BlockchainService blockchainService;
  private static EntityManagerService emService;
  private static UserService userService;
  private static CommunityRepository communityRepository;
  private static UserRepository userRepository;


  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Please specify Commons OS wallet file as first argument...");
      return;
    }

    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override protected void configure() {
        bind(Web3j.class).toProvider(Web3jProvider.class);
        bind(ObjectMapper.class).toInstance(new ObjectMapper());
      }
    });

    blockchainService = injector.getInstance(BlockchainService.class);
    emService = injector.getInstance(EntityManagerService.class);
    userService = injector.getInstance(UserService.class);
    communityRepository = injector.getInstance(CommunityRepository.class);
    userRepository = injector.getInstance(UserRepository.class);

    System.out.println("Creating new community... \n");

    System.out.print("Please unlock Commons OS wallet: ");
    Scanner scanner = new Scanner(System.in);
    String password = scanner.nextLine();
    Credentials commonsos = WalletUtils.loadCredentials(password, new File(args[0]));

    System.out.print("Please specify community name: ");
    String communityName = scanner.nextLine();
    System.out.print("Please enter token symbol: ");
    String tokenSymbol = scanner.nextLine();
    System.out.print("Please enter token name: ");
    String tokenName = scanner.nextLine();

    System.out.print("Please enter number of prepaid community transactions: ");
    int numberOfPrepaidTransactions = scanner.nextInt();
    scanner.nextLine();

    System.out.print("Please enter admin username (min 4): ");
    String adminUsername = scanner.nextLine();

    System.out.print("Please enter admin password (min 8): ");
    String adminPassword = scanner.nextLine();

    System.out.print("Please repeat admin password: ");
    String adminPassword2 = scanner.nextLine();

    if (!adminPassword.equals(adminPassword2)) {
      System.out.println("Passwords do not match");
      return;
    }

    System.out.print("Please enter admin first name: ");
    String adminFirstName = scanner.nextLine();

    System.out.print("Please enter admin last name: ");
    String adminLastName = scanner.nextLine();

    System.out.print("Please enter admin location: ");
    String adminLocation = scanner.nextLine();

    System.out.print("Please enter admin description: ");
    String adminDescription = scanner.nextLine();

    System.out.println("===============================");
    System.out.println("Community name:\t"+communityName);
    System.out.println("Token name:\t"+tokenName);
    System.out.println("Token symbol:\t"+tokenSymbol);
    System.out.println("Number of prepaid transactions:\t"+numberOfPrepaidTransactions);
    System.out.println("Admin username:\t"+adminUsername);
    System.out.println("Admin first name:\t"+adminFirstName);
    System.out.println("Admin last name:\t"+adminLastName);
    System.out.println("Admin location:\t"+adminLocation);
    System.out.println("Admin description:\t"+adminDescription);

    System.out.println("Is everything correct? Press 'Y' to create community");
    if (!scanner.nextLine().equals("Y")) {
      System.out.println("Exiting!");
      return;
    }

    User admin = emService.runInTransaction(() -> userService.create(new AccountCreateCommand()
      .setUsername(adminUsername)
      .setPassword(adminPassword)
      .setFirstName(adminFirstName)
      .setLastName(adminLastName)
      .setLocation(adminLocation))
      .setAdmin(true)
      .setDescription(adminDescription));

    blockchainService.transferEther(commonsos, admin.getWalletAddress(), initialEtherAmountForAdmin(numberOfPrepaidTransactions));
    createCommunity(admin, communityName, tokenSymbol, tokenName);

    System.out.println("Done!");
  }

  private static BigInteger initialEtherAmountForAdmin(int transactionCount) {
    return TOKEN_DEPLOYMENT_GAS_LIMIT.add(BigInteger.valueOf(transactionCount).multiply(TOKEN_TRANSFER_GAS_LIMIT)).multiply(GAS_PRICE);
  }

  private static Community createCommunity(User admin, String name, String tokenSymbol, String tokenName) {
    String tokenAddress = blockchainService.createToken(admin, tokenSymbol, tokenName);
    Community community = emService.runInTransaction(() -> communityRepository.create(new Community().setName(name).setTokenContractAddress(tokenAddress)));

    admin.setCommunityId(community.getId());
    emService.runInTransaction(() -> userRepository.update(admin));
    return community;
  }
}
