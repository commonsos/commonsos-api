package commonsos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import javax.inject.Provider;
import java.time.Instant;

public class GsonProvider implements Provider<Gson>{
  @Override public Gson get() {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(Instant.class, (JsonSerializer<Instant>)(src, typeOfSrc, context) -> new Gson().toJsonTree(src.toString()));
    builder.registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>)(src, typeOfSrc, context) -> Instant.parse(src.getAsString()));
    return builder.create();
  }
}
