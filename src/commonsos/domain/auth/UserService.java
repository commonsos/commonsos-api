package commonsos.domain.auth;

import commonsos.AuthenticationException;
import commonsos.BadRequestException;
import commonsos.DisplayableException;
import commonsos.ForbiddenException;
import commonsos.domain.blockchain.BlockchainService;
import commonsos.domain.community.Community;
import commonsos.domain.community.CommunityService;
import commonsos.domain.transaction.TransactionService;
import org.web3j.crypto.Credentials;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static commonsos.domain.blockchain.BlockchainService.GAS_PRICE;
import static commonsos.domain.blockchain.BlockchainService.TOKEN_TRANSFER_GAS_LIMIT;
import static java.util.stream.Collectors.toList;

@Singleton
public class UserService {
  public static final String WALLET_PASSWORD = "test";
  @Inject UserRepository repository;
  @Inject BlockchainService blockchainService;
  @Inject TransactionService transactionService;
  @Inject PasswordService passwordService;
  @Inject CommunityService communityService;

  public User checkPassword(String username, String password) {
    User user = repository.findByUsername(username).orElseThrow(AuthenticationException::new);
    if (!passwordService.passwordMatchesHash(password, user.getPasswordHash())) throw new AuthenticationException();
    return user;
  }

  public UserPrivateView privateView(User user) {
    BigDecimal balance = transactionService.balance(user);
    return new UserPrivateView()
      .setId(user.getId())
      .setAdmin(user.isAdmin())
      .setBalance(balance)
      .setFullName(fullName(user))
      .setLocation(user.location)
      .setDescription(user.getDescription())
      .setAvatarUrl(user.getAvatarUrl());
  }

  public UserPrivateView privateView(User currentUser, Long userId) {
    if (!currentUser.getId().equals(userId) && !currentUser.isAdmin()) throw new ForbiddenException();
    User user = repository.findById(userId).orElseThrow(ForbiddenException::new);
    return privateView(user);
  }

  private String fullName(User user) {
    return String.format("%s %s", user.getLastName(), user.getFirstName());
  }

  public User create(AccountCreateCommand command) {
    validate(command);
    Community community = null;
    if (repository.findByUsername(command.getUsername()).isPresent()) throw new DisplayableException("error.usernameTaken");
    if (command.getCommunityId() != null) {
      community = community(command);
    }

    User user = new User()
      .setCommunityId(command.getCommunityId())
      .setUsername(command.getUsername())
      .setPasswordHash(passwordService.hash(command.getPassword()))
      .setFirstName(command.getFirstName())
      .setLastName(command.getLastName())
      .setDescription(command.getDescription())
      .setLocation(command.getLocation());

    String wallet = blockchainService.createWallet(WALLET_PASSWORD);
    Credentials credentials = blockchainService.credentials(wallet, WALLET_PASSWORD);

    user.setWallet(wallet);
    user.setWalletAddress(credentials.getAddress());

    if (community != null) {
      User admin = walletUser(community);
      blockchainService.transferEther(admin, user.getWalletAddress(), TOKEN_TRANSFER_GAS_LIMIT.multiply(BigInteger.TEN).multiply(GAS_PRICE));
      blockchainService.delegateUser(user, admin);
    }

    return repository.create(user);
  }

  public User walletUser(Community community) {
    return repository.findAdminByCommunityId(community.getId());
  }

  private Community community(AccountCreateCommand command) {
    return communityService.community(command.getCommunityId());
  }

  private void validate(AccountCreateCommand command) {
    if (command.getUsername() == null || command.getUsername().length() < 4) throw new BadRequestException();
    if (command.getPassword() == null || command.getPassword().length() < 8) throw new BadRequestException();
    if (command.getFirstName() == null || command.getFirstName().length() < 1) throw new BadRequestException();
    if (command.getLastName() == null || command.getLastName().length() < 1) throw new BadRequestException();
  }

  public UserView view(Long id) {
    return view(user(id));
  }

  public User user(Long id) {
    return repository.findById(id).orElseThrow(BadRequestException::new);
  }

  public UserView view(User user) {
    return new UserView()
      .setId(user.getId())
      .setFullName(fullName(user))
      .setLocation(user.getLocation())
      .setDescription(user.getDescription())
      .setAvatarUrl(user.getAvatarUrl());
  }

  public List<UserView> searchUsers(User user, String query) {
    if (!user.isAdmin()) throw new ForbiddenException();
    return repository.search(user.getCommunityId(), query).stream().map(this::view).collect(toList());
  }

  public void setAvatar(User user, String url) {
    user.setAvatarUrl(url);
    repository.update(user);
  }
}
