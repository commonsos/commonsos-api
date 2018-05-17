package commonsos.controller.community;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class CommunityView {
  private Long id;
  private String name;
}
