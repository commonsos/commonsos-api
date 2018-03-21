package commonsos.domain.ad;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AdRepositoryTest {

  @InjectMocks AdRepository repository;

  @Test
  public void create_assings_id() {
    Ad ad = new Ad().setTitle("title").setDescription("description").setPoints(new BigDecimal("123.456")).setLocation("location").setCreatedBy("user id");

    repository.create(ad);

    assertTrue(repository.ads.contains(ad));
    assertEquals("3", ad.getId());
  }

  @Test
  public void list() {
    repository.list();
  }

  @Test
  public void findById_never_returns_null() {
    repository.ads = emptyList();

    assertFalse(repository.find("unknown").isPresent());
  }

  @Test
  public void findById_wraps_with__optional() {
    Ad ad = new Ad().setId("adId");
    repository.ads = asList(ad);

    assertEquals(ad, repository.find("adId").get());
  }

  @Test
  public void save_does_nothing() {
    repository.ads = asList();
    Ad ad = new Ad().setId("adId");

    repository.save(ad);

    assertEquals(0, repository.ads.size());
  }
}