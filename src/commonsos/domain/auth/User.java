package commonsos.domain.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

@Entity @Table(name = "users")
@Getter @Setter @Accessors(chain=true) @EqualsAndHashCode @ToString
public class User {
  @Id @GeneratedValue(strategy = IDENTITY) Long id;
  Long communityId;
  boolean admin;
  String username;
  String passwordHash;
  String firstName;
  String lastName;
  String description;
  String location;
  String avatarUrl;
  String wallet;
  String walletAddress;
}
