package commonsos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import javax.inject.Provider;
import java.time.OffsetDateTime;

public class GsonProvider implements Provider<Gson>{
  @Override public Gson get() {
    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(OffsetDateTime.class, (JsonSerializer<OffsetDateTime>)(src, typeOfSrc, context) -> new Gson().toJsonTree(src.toString()));
    builder.registerTypeAdapter(OffsetDateTime.class, (JsonDeserializer<OffsetDateTime>)(src, typeOfSrc, context) -> OffsetDateTime.parse(src.getAsString()));
    return builder.create();
  }
}
