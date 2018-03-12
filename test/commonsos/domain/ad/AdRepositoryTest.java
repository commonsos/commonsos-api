package commonsos.domain.ad;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)  @Ignore("Until database integration is implemented")
public class AdRepositoryTest {

  @InjectMocks AdRepository repository;

  @Test
  public void create() {
    repository.create(new Ad().setTitle("title").setDescription("description").setPoints(new BigDecimal("123.456")).setLocation("location"));
  }

  @Test
  public void list() {
    repository.list();
  }

}