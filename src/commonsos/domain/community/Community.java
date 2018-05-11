package commonsos.domain.community;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity @Table(name = "communities")
@Getter @Setter @Accessors(chain=true)
public class Community {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
  String name;
  String tokenContractId;
}
