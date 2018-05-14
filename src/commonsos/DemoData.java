package commonsos;

import commonsos.domain.ad.Ad;
import commonsos.domain.ad.AdCreateCommand;
import commonsos.domain.ad.AdService;
import commonsos.domain.auth.AccountCreateCommand;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserRepository;
import commonsos.domain.auth.UserService;
import commonsos.domain.blockchain.BlockchainService;
import commonsos.domain.community.Community;
import commonsos.domain.community.CommunityRepository;
import commonsos.domain.message.MessagePostCommand;
import commonsos.domain.message.MessageService;
import commonsos.domain.message.MessageThreadView;
import commonsos.domain.transaction.TransactionCreateCommand;
import commonsos.domain.transaction.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.Credentials;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;

import static commonsos.domain.ad.AdType.GIVE;
import static commonsos.domain.ad.AdType.WANT;
import static commonsos.domain.blockchain.BlockchainService.GAS_PRICE;
import static commonsos.domain.blockchain.BlockchainService.TOKEN_TRANSFER_GAS_LIMIT;
import static commonsos.domain.blockchain.TokenERC20Deployer.TOKEN_DEPLOYMENT_GAS_LIMIT;

@Singleton
@Slf4j
public class DemoData {

  @Inject EntityManagerService emService;
  @Inject UserService userService;
  @Inject UserRepository userRepository;
  @Inject TransactionService transactionService;
  @Inject AdService adService;
  @Inject MessageService messageService;
  @Inject BlockchainService blockchainService;
  @Inject CommunityRepository communityRepository;

  public void install() {

    if (!emService.get().createQuery("FROM User", User.class).setMaxResults(1).getResultList().isEmpty()) return;

    User admin = emService.runInTransaction(() -> userService.create(new AccountCreateCommand().setUsername("admin").setPassword("secret00").setFirstName("Coordinator").setLastName("Community").setLocation("Kaga, Ishikawa Prefecture, Japan"))
      .setAdmin(true).setAvatarUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPlkwhBse_JCK37_0WA3m_PHUpFncOVLM0s0c4cCqpV27UteuJ")
      .setDescription("I'm a coordinator of a my community. Contact me if you have problem to solve."));

    Credentials commonsos = commonsosCredentials();
    blockchainService.transferEther(commonsos, admin.getWalletAddress(),
      TOKEN_DEPLOYMENT_GAS_LIMIT.add(
        new BigInteger("1000").multiply(TOKEN_TRANSFER_GAS_LIMIT).multiply(GAS_PRICE))
    );

    log.info("Admin ether balance (WEI) is " + blockchainService.balance(admin.getWalletAddress()));

    String tokenAddress = blockchainService.createToken(admin, "KAGA", "Kaga coin");

    Community community = emService.runInTransaction(() -> communityRepository.create(new Community().setName("Kaga").setTokenContractId(tokenAddress)));

    admin.setCommunityId(community.getId());
    emService.runInTransaction(() -> userRepository.update(admin));

    User worker = emService.runInTransaction(() -> userService.create(new AccountCreateCommand().setUsername("worker").setPassword("secret00").setFirstName("Haruto").setLastName("Sato").setLocation("Shibuya, Tokyo, Japan"))
      .setAvatarUrl("https://image.jimcdn.com/app/cms/image/transf/none/path/s09a03e3ad80f8a02/image/i788e42d25ed4115e/version/1493969515/image.jpg")
      .setDescription("I am an Engineer, currently unemployed. I like helping elderly people, I can help with daily chores.").setCommunityId(community.getId()));
    blockchainService.transferEther(admin, worker.getWalletAddress(), TOKEN_TRANSFER_GAS_LIMIT.multiply(BigInteger.TEN).multiply(GAS_PRICE));

    User elderly1 = emService.runInTransaction(() -> userService.create(new AccountCreateCommand().setUsername("elderly1").setPassword("secret00").setFirstName("Riku").setLastName("Suzuki").setLocation("Kaga, Ishikawa Prefecture, Japan"))
      .setAvatarUrl("https://i.pinimg.com/originals/df/5c/70/df5c70b3b4895c4d9424de3845771182.jpg")
      .setDescription("I'm a retired person. I need personal assistance daily basis.").setCommunityId(community.getId()));
    blockchainService.transferEther(admin, elderly1.getWalletAddress(), TOKEN_TRANSFER_GAS_LIMIT.multiply(BigInteger.TEN).multiply(GAS_PRICE));

    User elderly2 = emService.runInTransaction(() -> userService.create(new AccountCreateCommand().setUsername("elderly2").setPassword("secret00").setFirstName("Haru").setLastName("Takahashi").setLocation("Kaga, Ishikawa Prefecture, Japan"))
      .setAvatarUrl("https://qph.fs.quoracdn.net/main-qimg-42b85e5f162e21ce346da83e8fa569bd-c").setDescription("Just jump in and lets play poker!").setCommunityId(community.getId()));
    blockchainService.transferEther(admin, elderly2.getWalletAddress(), TOKEN_TRANSFER_GAS_LIMIT.multiply(BigInteger.TEN).multiply(GAS_PRICE));

    transactionService.create(admin, new TransactionCreateCommand().setAmount(new BigDecimal("2000")).setBeneficiaryId(elderly1.getId()).setDescription("Funds from municipality"));
    transactionService.create(admin, new TransactionCreateCommand().setAmount(new BigDecimal("2000")).setBeneficiaryId(elderly2.getId()).setDescription("Funds from municipality"));

    Ad workerAd = emService.runInTransaction(() -> adService.create(worker, new AdCreateCommand()
      .setType(GIVE)
      .setTitle("House cleaning")
      .setDescription("Vacuum cleaning, moist cleaning, floors etc")
      .setAmount(new BigDecimal("1299.01"))
      .setLocation("Kaga city")
      .setPhotoUrl("/static/temp/sample-photo-apartment1.jpg"))
    );

    Ad elderly1Ad = emService.runInTransaction(() -> adService.create(elderly1, new AdCreateCommand()
      .setType(WANT)
      .setTitle("Shopping agent")
      .setDescription("Thank you for reading this article. I had traffic accident last year and chronic pain on left leg\uD83D\uDE22 I want anyone to help me by going shopping to a grocery shop once a week.")
      .setAmount(new BigDecimal("300"))
      .setLocation("Kumasakamachi 熊坂町")
      .setPhotoUrl("/static/temp/shop.jpeg")
    ));

    Ad elderly2Ad = emService.runInTransaction(() -> adService.create(elderly2, new AdCreateCommand()
      .setType(WANT)
      .setTitle("小川くん、醤油かってきて")
      .setDescription("刺し身買ってきたから")
      .setAmount(new BigDecimal("20"))
      .setLocation("kaga")
      .setPhotoUrl("/static/temp/soy.jpeg")
    ));

    MessageThreadView workerAdElderly1Thread = emService.runInTransaction(() -> messageService.threadForAd(elderly1, workerAd.getId()));
    messageService.postMessage(elderly1, new MessagePostCommand().setThreadId(workerAdElderly1Thread.getId()).setText("Hello!"));
    messageService.postMessage(elderly1, new MessagePostCommand().setThreadId(workerAdElderly1Thread.getId()).setText("I would like you to do cleaning in my appartement"));
    messageService.postMessage(worker, new MessagePostCommand().setThreadId(workerAdElderly1Thread.getId()).setText("Hi, what about tomorrow in the afternoon?"));
    messageService.postMessage(elderly1, new MessagePostCommand().setThreadId(workerAdElderly1Thread.getId()).setText("But I have a very little appartement, could it be cheaper?"));
    messageService.postMessage(worker, new MessagePostCommand().setThreadId(workerAdElderly1Thread.getId()).setText("No problem, it will be special price for you: 999.99"));
    transactionService.create(elderly1, new TransactionCreateCommand().setBeneficiaryId(worker.getId()).setAdId(workerAd.getId()).setDescription("Ad: House cleaning (agreed price)").setAmount(new BigDecimal("999.99")));


    MessageThreadView workerAdElderly2Thread = emService.runInTransaction(() -> messageService.threadForAd(elderly2, workerAd.getId()));
    messageService.postMessage(elderly2, new MessagePostCommand().setThreadId(workerAdElderly2Thread.getId()).setText("Hi! Would like to arrange cleaning on a weekly basis"));
    messageService.postMessage(worker, new MessagePostCommand().setThreadId(workerAdElderly2Thread.getId()).setText("Hi! Ok, would it be ok to do the first cleaning next Tuesday?"));
    messageService.postMessage(elderly2, new MessagePostCommand().setThreadId(workerAdElderly2Thread.getId()).setText("Yes, waiting for you."));
    transactionService.create(elderly2, new TransactionCreateCommand().setBeneficiaryId(worker.getId()).setAdId(workerAd.getId()).setDescription("Ad: House cleaning").setAmount(new BigDecimal("1299.01")));

    MessageThreadView elderly1AdThread = emService.runInTransaction(() -> messageService.threadForAd(elderly2, elderly1Ad.getId()));
    messageService.postMessage(elderly2, new MessagePostCommand().setThreadId(elderly1AdThread.getId()).setText("Hi, I can bring you some food from the shop"));
    transactionService.create(elderly1, new TransactionCreateCommand().setBeneficiaryId(elderly2.getId()).setAdId(elderly1Ad.getId()).setDescription("Ad: Shopping agent").setAmount(new BigDecimal("300")));

    MessageThreadView elderly2AdThread = emService.runInTransaction(() -> messageService.threadForAd(worker, elderly2Ad.getId()));
    transactionService.create(elderly2, new TransactionCreateCommand().setBeneficiaryId(worker.getId()).setAdId(elderly2Ad.getId()).setDescription("Ad: 小川くん、醤油かってきて").setAmount(BigDecimal.TEN.add(BigDecimal.TEN)));
  }

  private Credentials commonsosCredentials() {
    String wallet = "{\"address\":\"14063fb2a2e24cf80081a946953159d86e88c36c\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"bb5f7281afa8d00b591b3a5018cd4e326e77203f67b48c4c9b87a9d8ead1ff24\",\"cipherparams\":{\"iv\":\"2d2d05001ca458bd568dbcba9ca1baf3\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"5c24461dd75406c8c129427aa2b8bff76138b671ea3054752f939257d0bf6443\"},\"mac\":\"236d63008c0241deb894569642d1fadf0d8df41dde4953c4fc0c7946a808add5\"},\"id\":\"8151538e-644f-4310-bffe-436b38075a04\",\"version\":3}";
    return blockchainService.credentials(wallet, "test");
  }
}
