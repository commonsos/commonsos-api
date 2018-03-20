package commonsos;

import com.google.gson.Gson;
import org.junit.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class GsonProviderTest {

  private Gson gson = new GsonProvider().get();

  @Test
  public void serializesOffsetDateTime() {
    assertThat(gson.toJson(OffsetDateTime.of(2010, 3, 10, 1, 2, 3, 123456789, ZoneOffset.ofHours(-2))))
      .isEqualTo("\"2010-03-10T01:02:03.123456789-02:00\"");
  }

  @Test
  public void deserializesOffsetDateTime() {
    assertThat(gson.fromJson("\"2010-03-10T01:02:03.123456789-02:00\"", OffsetDateTime.class))
      .isEqualTo(OffsetDateTime.of(2010, 3, 10, 1, 2, 3, 123456789, ZoneOffset.ofHours(-2)));
  }
}