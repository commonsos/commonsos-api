package commonsos.domain.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class User {
  String id;
  boolean admin;
  String username;
  String passwordHash;
  String firstName;
  String lastName;
  String location;
}
