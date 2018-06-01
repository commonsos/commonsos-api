package commonsos.domain.auth;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
@Slf4j
public class ImageService {

  Map<String, byte[]> images = new HashMap<>();

  public String uploadImage(byte[] bytes) {
    String random = UUID.randomUUID().toString();
    String url = "/s3/" + random;
    log.info("new image url " + url);
    images.put(url, bytes);
    return "/api"+url;
  }

  public byte[] image(String url) {
    log.info("asked for " + url);
    return images.get(url);
  }
}
