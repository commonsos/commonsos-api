package commonsos.domain.ad;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AdRepositoryTest {

  @InjectMocks AdRepository repository;

  @Test
  public void create_assignsId() {
    Ad newAd = new Ad();
    repository.create(newAd);

    assertTrue(repository.ads.contains(newAd));
    assertEquals("0", newAd.getId());
  }

  @Test
  public void list() {
    repository.list();
  }

  @Test
  public void findById_neverReturnsNull() {
    repository.ads = emptyList();

    assertFalse(repository.find("unknown").isPresent());
  }

  @Test
  public void findById_wrapsWithOptional() {
    Ad ad = new Ad().setId("adId");
    repository.ads = asList(ad);

    assertEquals(ad, repository.find("adId").get());
  }

  @Test
  public void save_doesNothing() {
    repository.ads = asList();
    Ad ad = new Ad().setId("adId");

    repository.save(ad);

    assertEquals(0, repository.ads.size());
  }
}