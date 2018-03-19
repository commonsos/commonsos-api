package commonsos.domain.agreement;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AgreementRepository {

  List<Agreement> agreements = new ArrayList<>();

  public void create(Agreement agreement) {
    agreements.add(agreement);
  }
}
