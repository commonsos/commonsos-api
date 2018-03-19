package commonsos.domain.agreement;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class AgreementRepository {

  List<Agreement> agreements = new ArrayList<>();

  public void create(Agreement agreement) {
    agreements.add(agreement);
  }

  public List<Agreement> consumedBy(String userId) {
    return agreements.stream().filter(a -> a.getConsumerId().equals(userId)).collect(Collectors.toList());
  }
}
