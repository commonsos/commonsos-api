package commonsos;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
  Gson gson = new Gson();

  @Override public String render(Object model) throws Exception {
    return gson.toJson(model);
  }
}
