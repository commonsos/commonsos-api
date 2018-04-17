package commonsos.domain.auth;

import commonsos.AuthenticationException;
import commonsos.BadRequestException;
import commonsos.DisplayableException;
import commonsos.ForbiddenException;
import commonsos.domain.agreement.AccountCreateCommand;
import commonsos.domain.transaction.TransactionService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Singleton
public class UserService {
  @Inject UserRepository repository;
  @Inject TransactionService transactionService;
  @Inject PasswordService passwordService;

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
      .setLocation(user.location);
  }

  public UserPrivateView privateView(User currentUser, String userId) {
    if (!currentUser.getId().equals(userId) && !currentUser.isAdmin()) throw new ForbiddenException();
    User user = repository.findById(userId).orElseThrow(ForbiddenException::new);
    return privateView(user);
  }

  private String fullName(User user) {
    return String.format("%s %s", user.getLastName(), user.getFirstName());
  }

  public User create(AccountCreateCommand command) {
    validate(command);

    if (repository.findByUsername(command.getUsername()).isPresent()) throw new DisplayableException("Username is already taken");

    User user = new User()
      .setUsername(command.getUsername())
      .setPasswordHash(passwordService.hash(command.getPassword()))
      .setFirstName(command.getFirstName())
      .setLastName(command.getLastName())
      .setLocation(command.getLocation());

    return repository.create(user);
  }

  private void validate(AccountCreateCommand command) {
    if (command.getUsername() == null || command.getUsername().length() < 4) throw new BadRequestException();
    if (command.getPassword() == null || command.getPassword().length() < 8) throw new BadRequestException();
    if (command.getFirstName() == null || command.getFirstName().length() < 1) throw new BadRequestException();
    if (command.getLastName() == null || command.getLastName().length() < 1) throw new BadRequestException();
  }

  public UserView view(String id) {
    return view(user(id));
  }

  private User user(String id) {
    return repository.findById(id).orElseThrow(BadRequestException::new);
  }

  public UserView view(User user) {
    return new UserView().setId(user.getId()).setFullName(fullName(user));
  }

  public List<UserView> searchUsers(User user, String query) {
    if (!user.isAdmin()) throw new ForbiddenException();
    return repository.search(query).stream().map(this::view).collect(toList());
  }
}
