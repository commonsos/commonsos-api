package commonsos;

import java.util.HashMap;
import java.util.Map;

public class TestId {

  private static Map<String, Long> ids = new HashMap<>();

  public static Long id(String value) {
    return ids.computeIfAbsent(value, s -> (long)ids.values().size());
  }
}
