package commonsos;

import com.google.gson.Gson;
import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class GsonProviderTest {

  private Gson gson = new GsonProvider().get();

  Instant instant = Instant.parse("2010-03-10T01:02:03Z");

  @Test
  public void serializesInstant() {

    assertThat(gson.toJson(instant)).isEqualTo("\"2010-03-10T01:02:03Z\"");
  }

  @Test
  public void deserializesInstant() {
    assertThat(gson.fromJson("\"2010-03-10T01:02:03Z\"", Instant.class)).isEqualTo(instant);
  }
}