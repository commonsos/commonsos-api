package commonsos;

import commonsos.domain.ad.AdCreateCommand;
import commonsos.domain.ad.AdService;
import commonsos.domain.auth.AccountCreateCommand;
import commonsos.domain.auth.User;
import commonsos.domain.auth.UserService;
import commonsos.domain.transaction.Transaction;
import commonsos.domain.transaction.TransactionService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static commonsos.domain.ad.AdType.GIVE;
import static commonsos.domain.ad.AdType.WANT;
import static java.time.temporal.ChronoUnit.HOURS;

@Singleton
public class DemoData {

  @Inject UserService userService;
  @Inject TransactionService transactionService;
  @Inject AdService adService;

  public void install() {

    User worker = userService.create(new AccountCreateCommand().setUsername("worker").setPassword("secret00").setFirstName("Haruto").setLastName("Sato").setLocation("Shibuya, Tokyo, Japan"))
      .setAvatarUrl("https://image.jimcdn.com/app/cms/image/transf/none/path/s09a03e3ad80f8a02/image/i788e42d25ed4115e/version/1493969515/image.jpg")
      .setDescription("I am an Engineer, currently unemployed. I like helping elderly people, I can help with daily chores.");

    User elderly1 = userService.create(new AccountCreateCommand().setUsername("elderly1").setPassword("secret00").setFirstName("Riku").setLastName("Suzuki").setLocation("Kaga, Ishikawa Prefecture, Japan"))
      .setAvatarUrl("https://i.pinimg.com/originals/df/5c/70/df5c70b3b4895c4d9424de3845771182.jpg")
      .setDescription("I'm a retired person. I need personal assistance daily basis.");

    User elderly2 = userService.create(new AccountCreateCommand().setUsername("elderly2").setPassword("secret00").setFirstName("Haru").setLastName("Takahashi").setLocation("Kaga, Ishikawa Prefecture, Japan"))
      .setAvatarUrl("https://qph.fs.quoracdn.net/main-qimg-42b85e5f162e21ce346da83e8fa569bd-c").setDescription("Just jump in and lets play poker!");

    User admin = userService.create(new AccountCreateCommand().setUsername("admin").setPassword("secret00").setFirstName("Coordinator").setLastName("Community").setLocation("Kaga, Ishikawa Prefecture, Japan"))
      .setAdmin(true).setAvatarUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPlkwhBse_JCK37_0WA3m_PHUpFncOVLM0s0c4cCqpV27UteuJ")
      .setDescription("I'm a coordinator of a my community. Contact me if you have problem to solve.");

    User bank = userService.create(new AccountCreateCommand().setUsername(UUID.randomUUID().toString()).setPassword(UUID.randomUUID().toString()).setFirstName("Bank").setLastName(" "))
      .setAdmin(true).setAvatarUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPlkwhBse_JCK37_0WA3m_PHUpFncOVLM0s0c4cCqpV27UteuJ").setDescription("Not a real user.");

    transactionService.create(new Transaction().setRemitterId(bank.getId()).setAmount(new BigDecimal("10000000")).setBeneficiaryId(admin.getId()).setDescription("Initial emission to community").setCreatedAt(OffsetDateTime.now().minus(1, HOURS)));
    transactionService.create(new Transaction().setRemitterId(admin.getId()).setAmount(new BigDecimal("2000")).setBeneficiaryId(elderly1.getId()).setDescription("Funds from municipality").setCreatedAt(OffsetDateTime.now()));
    transactionService.create(new Transaction().setRemitterId(admin.getId()).setAmount(new BigDecimal("2000")).setBeneficiaryId(elderly2.getId()).setDescription("Funds from municipality").setCreatedAt(OffsetDateTime.now()));

    adService.create(worker, new AdCreateCommand()
        .setType(GIVE)
        .setTitle("House cleaning")
        .setDescription("Vacuum cleaning, moist cleaning, floors etc")
        .setAmount(new BigDecimal("1299.01"))
        .setLocation("Kaga city")
      ).setPhotoUrl("/static/temp/sample-photo-apartment1.jpg");

    adService.create(elderly1, new AdCreateCommand()
        .setType(WANT)
        .setTitle("Shopping agent")
        .setDescription("Thank you for reading this article. I had traffic accident last year and chronic pain on left leg\uD83D\uDE22 I want anyone to help me by going shopping to a grocery shop once a week.")
        .setAmount(new BigDecimal("300"))
        .setLocation("Kumasakamachi 熊坂町")
      ).setPhotoUrl("/static/temp/shop.jpeg");

    adService.create(elderly2, new AdCreateCommand()
        .setType(WANT)
        .setTitle("小川くん、醤油かってきて")
        .setDescription("刺し身買ってきたから")
        .setAmount(new BigDecimal("1"))
        .setLocation("kaga")
      ).setPhotoUrl("/static/temp/soy.jpeg");
  }
}
