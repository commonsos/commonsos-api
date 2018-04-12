package commonsos.domain.auth;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class PasswordServiceTest {

  PasswordService service = spy(new PasswordService());

  @Test
  public void hash() {
    doReturn(new byte[]{0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,12}).when(service).generatePasswordSalt();

    String hash = service.hash("secret");

    assertThat(hash).isEqualTo("AAECAwQFBgcICQABAgMEBQYHCAkAAQIDBAUGBwgJDA==|3wRb8pstRs3VEQ02hedNcVJJ6m2jfdcQ9aCAV+xKWyA=|10");
  }

  @Test
  public void verifyHash() {
    String passwordHash = "AAECAwQFBgcICQABAgMEBQYHCAkAAQIDBAUGBwgJDA==|3wRb8pstRs3VEQ02hedNcVJJ6m2jfdcQ9aCAV+xKWyA=|10";

    assertThat(service.passwordMatchesHash("secret", passwordHash)).isTrue();
  }

  @Test
  public void verifyHash_wrongPassword() {
    String passwordHash = "AAECAwQFBgcICQABAgMEBQYHCAkAAQIDBAUGBwgJDA==|3wRb8pstRs3VEQ02hedNcVJJ6m2jfdcQ9aCAV+xKWyA=|10";

    assertThat(service.passwordMatchesHash("wrong", passwordHash)).isFalse();
  }
}