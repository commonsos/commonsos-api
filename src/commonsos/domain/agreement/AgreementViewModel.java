package commonsos.domain.agreement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter @Setter @Accessors(chain = true)
public class AgreementViewModel {
  String id;
  String title;
  String description;
  String location;
  BigDecimal amount;
  String transactionData;
}