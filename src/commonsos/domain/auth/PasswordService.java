package commonsos.domain.auth;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Singleton;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import static java.lang.Integer.parseInt;

@Singleton
public class PasswordService {

  private static final int KEY_LENGTH = 256;

  Base64.Encoder encoder = Base64.getEncoder();
  Base64.Decoder decoder = Base64.getDecoder();

  public String hash(String password) {
    byte[] salt = generatePasswordSalt();
    int iterations = 10;
    byte[] hash = hashPassword(password.toCharArray(), salt, iterations, KEY_LENGTH);
    return encoder.encodeToString(salt) + "|" + encoder.encodeToString(hash) + "|" + String.valueOf(iterations);
  }

  public boolean passwordMatchesHash(String password, String passwordHash) {
    String[] split = passwordHash.split("\\|");
    if (split.length != 3) return false;

    byte[] salt = decoder.decode(split[0]);
    byte[] expectedHash = decoder.decode(split[1]);
    int iterations = parseInt(split[2]);

    return Arrays.equals(expectedHash, hashPassword(password.toCharArray(), salt, iterations, KEY_LENGTH));
  }

  byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {
    try {
      SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
      PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
      SecretKey key = skf.generateSecret( spec );
      return key.getEncoded();

    } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
      throw new RuntimeException( e );
    }
  }

  byte[] generatePasswordSalt() {
    byte bytes[] = new byte[32];
    new SecureRandom().nextBytes(bytes);
    return bytes;
  }
}
