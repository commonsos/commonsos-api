package commonsos.domain.auth;

import commonsos.*;
import commonsos.controller.auth.DelegateWalletTask;
import commonsos.domain.blockchain.BlockchainService;
import commonsos.domain.community.Community;
import commonsos.domain.community.CommunityService;
import commonsos.domain.transaction.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.web3j.crypto.Credentials;

import java.io.ByteArrayInputStream;
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
  @Mock ImageService imageService;
  @Mock JobService jobsService;
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
    when(blockchainService.isConnected()).thenReturn(true);
    Community community = new Community().setId(id("community"));
    when(communityService.community(id("community"))).thenReturn(community);
    when(repository.create(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(passwordService.hash("secret78")).thenReturn("hash");
    when(blockchainService.createWallet(WALLET_PASSWORD)).thenReturn("wallet");
    Credentials credentials = mock(Credentials.class);
    when(credentials.getAddress()).thenReturn("wallet address");
    when(blockchainService.credentials("wallet", WALLET_PASSWORD)).thenReturn(credentials);
    User communityAdmin = new User().setAdmin(true);
    when(repository.findAdminByCommunityId(id("community"))).thenReturn(communityAdmin);

    User result = service.create(new AccountCreateCommand()
      .setUsername("user name")
      .setPassword("secret78")
      .setFirstName("first")
      .setLastName("last")
      .setDescription("description")
      .setLocation("Shibuya")
      .setCommunityId(id("community"))
    );

    User expectedUser = new User()
      .setUsername("user name")
      .setPasswordHash("hash")
      .setFirstName("first")
      .setLastName("last")
      .setDescription("description")
      .setLocation("Shibuya")
      .setWallet("wallet")
      .setWalletAddress("wallet address")
      .setCommunityId(id("community"));

    assertThat(result).isEqualTo(expectedUser);
    verify(repository).create(expectedUser);
    verify(jobsService).submit(result, new DelegateWalletTask(result, communityAdmin));
  }

  @Test
  public void create_executesDelegateWalletSynchronously() {
    AccountCreateCommand command = new AccountCreateCommand()
      .setCommunityId(id("community"))
      .setWaitUntilCompleted(true);
    doNothing().when(service).validate(command);
    when(blockchainService.isConnected()).thenReturn(true);
    Community community = new Community().setId(id("community"));
    when(communityService.community(id("community"))).thenReturn(community);
    when(repository.create(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(blockchainService.credentials(any(), any())).thenReturn(mock(Credentials.class));
    User communityAdmin = new User().setAdmin(true);
    when(repository.findAdminByCommunityId(id("community"))).thenReturn(communityAdmin);

    User result = service.create(command);

    verify(jobsService).execute(new DelegateWalletTask(result, communityAdmin));
  }

  @Test
  public void create_failFastIfBlockchainIsDown() {
    AccountCreateCommand command = new AccountCreateCommand();
    doNothing().when(service).validate(command);
    RuntimeException thrown = catchThrowableOfType(() -> service.create(command), RuntimeException.class);

    assertThat(thrown).hasMessage("Cannot create user, technical error with blockchain");
  }

  @Test(expected = BadRequestException.class)
  public void create_unknownCommunity() {
    when(blockchainService.isConnected()).thenReturn(true);
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
    when(blockchainService.isConnected()).thenReturn(true);
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
    when(blockchainService.isConnected()).thenReturn(true);
    when(repository.findByUsername("worker")).thenReturn(Optional.of(new User()));
    AccountCreateCommand command = validCommand();

    DisplayableException thrown = catchThrowableOfType(()-> service.create(command), DisplayableException.class);

    assertThat(thrown).hasMessage("error.usernameTaken");
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

  @Test
  public void searchUsers() {
    User user = new User().setId(1L);
    when(repository.search(user.getCommunityId(), "foobar")).thenReturn(asList(user));
    UserView userView = new UserView();
    when(service.view(user)).thenReturn(userView);

    List<UserView> users = service.searchUsers(new User().setId(2L), "foobar");

    assertThat(users).isEqualTo(asList(userView));
  }

  @Test
  public void searchUsers_excludesSearchingUser() {
    User myself = new User().setId(id("myself"));
    User other = new User().setId(id("other"));
    when(repository.search(myself.getCommunityId(), "foobar")).thenReturn(asList(myself, other));
    UserView userView = new UserView();
    when(service.view(other)).thenReturn(userView);

    List<UserView> users = service.searchUsers(myself, "foobar");

    assertThat(users).isEqualTo(asList(userView));
  }

  @Test
  public void walletUser() {
    User admin = new User();
    when(repository.findAdminByCommunityId(id("community"))).thenReturn(admin);

    User result = service.walletUser(new Community().setId(id("community")));

    assertThat(result).isEqualTo(admin);
  }

  @Test
  public void updateAvatar() {
    User user = new User().setAvatarUrl("/old");
    ByteArrayInputStream image = new ByteArrayInputStream(new byte[] {1, 2, 3});
    when(imageService.create(image)).thenReturn("/url");

    String result = service.updateAvatar(user, image);

    assertThat(result).isEqualTo("/url");
    assertThat(user.getAvatarUrl()).isEqualTo("/url");
    verify(repository).update(user);
    verify(imageService).delete("/old");
  }

  @Test
  public void updateAvatar_userHasNoAvatarYet() {
    User user = new User().setAvatarUrl(null);
    ByteArrayInputStream image = new ByteArrayInputStream(new byte[] {1, 2, 3});
    when(imageService.create(image)).thenReturn("/url");

    String result = service.updateAvatar(user, image);

    assertThat(result).isEqualTo("/url");
    assertThat(user.getAvatarUrl()).isEqualTo("/url");
    verify(repository).update(user);
    verify(imageService, never()).delete(any());
  }

  @Test
  public void userSession() {
    User user = new User().setId(id("user id")).setUsername("user name");

    assertThat(service.session(user)).isEqualTo(new UserSession().setUserId(id("user id")).setUsername("user name"));
  }

  @Test
  public void updateUser() {
    User user = new User().setFirstName("me").setLastName("myself").setDescription("nice");
    UserUpdateCommand command = new UserUpdateCommand().setFirstName("John").setLastName("Doe").setDescription("About me").setLocation("Nice place");

    service.updateUser(user, command);

    verify(repository).update(new User().setFirstName("John").setLastName("Doe").setDescription("About me").setLocation("Nice place"));
  }
}