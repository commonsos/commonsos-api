package commonsos.domain.ad;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.InputStream;

@Getter @Setter @Accessors(chain=true)
public class AdPhotoUpdateCommand {
  private Long adId;
  private InputStream photo;
}
