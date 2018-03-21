package commonsos.domain.agreement;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class AgreementRepository {

  List<Agreement> agreements = new ArrayList<>();

  public void create(Agreement agreement) {
    agreements.add(agreement.setId(String.valueOf(agreements.size())));
  }

  public List<Agreement> consumedBy(String userId) {
    return agreements.stream().filter(a -> a.getConsumerId().equals(userId)).collect(Collectors.toList());
  }

  public Optional<Agreement> find(String id) {
    return agreements.stream().filter(a -> a.getId().equals(id)).findAny();
  }

  public void update(Agreement agreement) {
    int index = agreements.indexOf(find(agreement.getId()).get());
    agreements.remove(index);
    agreements.add(index, agreement);
  }
}
