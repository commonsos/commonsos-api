package commonsos.domain.auth;

import commonsos.AuthenticationException;
import commonsos.BadRequestException;
import commonsos.DisplayableException;
import commonsos.ForbiddenException;
import commonsos.controller.community.CommunityView;
import commonsos.domain.blockchain.BlockchainService;
import commonsos.domain.community.CommunityService;
import commonsos.domain.transaction.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.web3j.crypto.Credentials;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static commonsos.TestId.id;
import static commonsos.domain.auth.UserService.WALLET_PASSWORD;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @Mock UserRepository repository;
  @Mock TransactionService transactionService;
  @Mock PasswordService passwordService;
  @Mock BlockchainService blockchainService;
  @Mock CommunityService communityService;
  @InjectMocks @Spy UserService service;

  @Test
  public void checkPassword_withValidUser() {
    User user = new User().setPasswordHash("hash");
    when(passwordService.passwordMatchesHash("secret", "hash")).thenReturn(true);
    when(repository.findByUsername("worker")).thenReturn(Optional.of(user));

    assertThat(service.checkPassword("worker", "secret")).isEqualTo(user);
  }

  @Test(expected = AuthenticationException.class)
  public void checkPassword_withInvalidUsername() {
    when(repository.findByUsername("invalid")).thenReturn(Optional.empty());

    service.checkPassword("invalid", "secret");
  }

  @Test(expected = AuthenticationException.class)
  public void checkPassword_withInvalidPassword() {
    when(repository.findByUsername("user")).thenReturn(Optional.of(new User().setPasswordHash("hash")));
    when(passwordService.passwordMatchesHash("wrong password", "hash")).thenReturn(false);

    service.checkPassword("user", "wrong password");

    verify(passwordService).passwordMatchesHash("wrong password", "hash");
  }

  @Test
  public void privateView() {
    User user = new User().setId(id("user id")).setFirstName("first").setLastName("last").setLocation("Shibuya")
      .setAvatarUrl("/avatar.png").setDescription("description").setAdmin(true);
    when(transactionService.balance(user)).thenReturn(BigDecimal.TEN);

    UserPrivateView view = service.privateView(user);

    assertThat(view.getId()).isEqualTo(id("user id"));
    assertThat(view.isAdmin()).isTrue();
    assertThat(view.getBalance()).isEqualTo(BigDecimal.TEN);
    assertThat(view.getFullName()).isEqualTo("last first");
    assertThat(view.getLocation()).isEqualTo("Shibuya");
    assertThat(view.getDescription()).isEqualTo("description");
    assertThat(view.getAvatarUrl()).isEqualTo("/avatar.png");
  }

  @Test
  public void privateView_adminAccessesOtherUser() {
    User currentUser = new User().setId(222L).setAdmin(true);
    User foundUser = new User().setId(111L);
    when(repository.findById(111L)).thenReturn(Optional.of(foundUser));
    UserPrivateView foundUserPrivateView = new UserPrivateView();
    doReturn(foundUserPrivateView).when(service).privateView(foundUser);

    assertThat(service.privateView(currentUser, 111L)).isSameAs(foundUserPrivateView);
  }

  @Test
  public void privateView_ownUser() {
    User currentUser = new User().setId(111L).setAdmin(false);
    User foundUser = new User().setId(111L);
    when(repository.findById(111L)).thenReturn(Optional.of(foundUser));
    UserPrivateView foundUserPrivateView = new UserPrivateView();
    doReturn(foundUserPrivateView).when(service).privateView(foundUser);

    assertThat(service.privateView(currentUser, 111L)).isSameAs(foundUserPrivateView);
  }

  @Test(expected = ForbiddenException.class)
  public void privateView_requiresAdminToAccessOtherUser() {
    User currentUser = new User().setId(222L).setAdmin(false);

    service.privateView(currentUser, 111L);
  }

  @Test
  public void view() {
    User user = new User().setId(id("user id")).setFirstName("first").setLastName("last").setLocation("Shibuya").setAvatarUrl("/avatar.png").setDescription("description");

    UserView view = service.view(user);

    assertThat(view.getId()).isEqualTo(id("user id"));
    assertThat(view.getFullName()).isEqualTo("last first");
    assertThat(view.getDescription()).isEqualTo("description");
    assertThat(view.getLocation()).isEqualTo("Shibuya");
    assertThat(view.getAvatarUrl()).isEqualTo("/avatar.png");
  }

  @Test
  public void create() {
    User createdUser = new User();
    when(repository.create(any())).thenReturn(createdUser);
    when(passwordService.hash("secret78")).thenReturn("hash");
    when(blockchainService.createWallet(WALLET_PASSWORD)).thenReturn("wallet");
    Credentials credentials = mock(Credentials.class);
    when(credentials.getAddress()).thenReturn("wallet address");
    when(blockchainService.credentials("wallet", WALLET_PASSWORD)).thenReturn(credentials);
    when(communityService.community(23L)).thenReturn(mock(CommunityView.class));

    User result = service.create(new AccountCreateCommand()
      .setUsername("user name")
      .setPassword("secret78")
      .setFirstName("first")
      .setLastName("last")
      .setDescription("description")
      .setLocation("Shibuya")
      .setCommunityId(23L)
    );

    assertThat(result).isEqualTo(createdUser);
    verify(repository).create(new User()
      .setUsername("user name")
      .setPasswordHash("hash")
      .setFirstName("first")
      .setLastName("last")
      .setDescription("description")
      .setLocation("Shibuya")
      .setWallet("wallet")
      .setWalletAddress("wallet address")
      .setCommunityId(23L)
    );
  }

  @Test(expected = BadRequestException.class)
  public void create_unknownCommunity() {
    when(communityService.community(23L)).thenThrow(BadRequestException.class);

    service.create(new AccountCreateCommand()
      .setUsername("user name")
      .setPassword("secret78")
      .setFirstName("first")
      .setLastName("last")
      .setDescription("description")
      .setLocation("Shibuya")
      .setCommunityId(23L)
    );
  }

  @Test
  public void create_communityIsOptional() {
    User createdUser = new User();
    when(repository.create(any())).thenReturn(createdUser);
    when(passwordService.hash("secret78")).thenReturn("hash");
    when(blockchainService.createWallet(WALLET_PASSWORD)).thenReturn("wallet");
    Credentials credentials = mock(Credentials.class);
    when(credentials.getAddress()).thenReturn("wallet address");
    when(blockchainService.credentials("wallet", WALLET_PASSWORD)).thenReturn(credentials);

    service.create(new AccountCreateCommand()
      .setUsername("user name")
      .setPassword("secret78")
      .setFirstName("first")
      .setLastName("last")
      .setDescription("description")
      .setLocation("Shibuya")
      .setCommunityId(null)
    );
  }

  @Test
  public void create_usernameAlreadyTaken() {
    when(repository.findByUsername("worker")).thenReturn(Optional.of(new User()));
    AccountCreateCommand command = validCommand();

    DisplayableException thrown = catchThrowableOfType(()-> service.create(command), DisplayableException.class);

    assertThat(thrown).hasMessage("Username is already taken");
  }

  @Test(expected = BadRequestException.class)
  public void create_validates_username() {
    service.create(validCommand().setUsername("123"));
  }

  @Test(expected = BadRequestException.class)
  public void create_validates_password() {
    service.create(validCommand().setPassword("1234567"));
  }

  @Test(expected = BadRequestException.class)
  public void create_validates_firstName() {
    service.create(validCommand().setFirstName(""));
  }

  @Test(expected = BadRequestException.class)
  public void create_validates_lastName() {
    service.create(validCommand().setLastName(""));
  }

  private AccountCreateCommand validCommand() {
    return new AccountCreateCommand()
        .setUsername("worker")
        .setPassword("secret78")
        .setFirstName("first")
        .setLastName("last");
  }

  @Test
  public void viewByUserId() {
    User user = new User();
    when(repository.findById(id("user id"))).thenReturn(Optional.of(user));
    UserView view = new UserView();
    doReturn(view).when(service).view(user);

    assertThat(service.view(id("user id"))).isEqualTo(view);
  }

  @Test(expected = BadRequestException.class)
  public void viewByUserId_userNotFound() {
    when(repository.findById(id("invalid id"))).thenReturn(Optional.empty());

    service.view(id("invalid id"));
  }

  @Test(expected=ForbiddenException.class)
  public void searchUsers_forbidden() {
    service.searchUsers(new User().setAdmin(false), "foobar");
  }

  @Test
  public void searchUsers() {
    User user = new User();
    when(repository.search("foobar")).thenReturn(asList(user));
    UserView userView = new UserView();
    when(service.view(user)).thenReturn(userView);

    List<UserView> users = service.searchUsers(new User().setAdmin(true), "foobar");

    assertThat(users).isEqualTo(asList(userView));
  }
}