package commonsos;

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

@Singleton
public class DemoData {

  @Inject UserService userService;
  @Inject TransactionService transactionService;

  public void install() {
    userService.create(new AccountCreateCommand().setUsername("worker").setPassword("secret00").setFirstName("Haruto").setLastName("Sato").setLocation("Shibuya, Tokyo, Japan"));
    User elderly1 = userService.create(new AccountCreateCommand().setUsername("elderly1").setPassword("secret00").setFirstName("Riku").setLastName("Suzuki").setLocation("Kaga, Ishikawa Prefecture, Japan"));
    User elderly2 = userService.create(new AccountCreateCommand().setUsername("elderly2").setPassword("secret00").setFirstName("Haru").setLastName("Takahashi").setLocation("Kaga, Ishikawa Prefecture, Japan"));
    User admin = userService.create(new AccountCreateCommand().setUsername("admin").setPassword("secret00").setFirstName("Coordinator").setLastName("Community").setLocation("Kaga, Ishikawa Prefecture, Japan"))
      .setAdmin(true);

    User bank = userService.create(new AccountCreateCommand().setUsername(UUID.randomUUID().toString()).setPassword(UUID.randomUUID().toString()).setFirstName("Bank").setLastName(" ")).setAdmin(true);

    transactionService.create(new Transaction().setRemitterId(bank.getId()).setAmount(new BigDecimal("10000000")).setBeneficiaryId(admin.getId()).setCreatedAt(OffsetDateTime.now()));
    transactionService.create(new Transaction().setRemitterId(admin.getId()).setAmount(new BigDecimal("2000")).setBeneficiaryId(elderly1.getId()).setDescription("Funds from municipality").setCreatedAt(OffsetDateTime.now()));
    transactionService.create(new Transaction().setRemitterId(admin.getId()).setAmount(new BigDecimal("2000")).setBeneficiaryId(elderly2.getId()).setDescription("Funds from municipality").setCreatedAt(OffsetDateTime.now()));
  }
}
