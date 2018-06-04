package commonsos;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;


public class ConfigurationTest {

  Configuration configuration = spy(new Configuration());

  @Test
  public void awsAccessKey() {
    doReturn("value1").when(configuration).environmentVariable("AWS_ACCESS_KEY");

    assertThat(configuration.awsAccessKey()).isEqualTo("value1");
  }

  @Test
  public void awsSecretKey() {
    doReturn("value2").when(configuration).environmentVariable("AWS_SECRET_KEY");

    assertThat(configuration.awsSecurityKey()).isEqualTo("value2");
  }

  @Test
  public void awwS3BucketName() {
    doReturn("value3").when(configuration).environmentVariable("AWS_S3_BUCKET_NAME", "commonsos-app");

    assertThat(configuration.awsS3BucketName()).isEqualTo("value3");
  }

  @Test(expected = RuntimeException.class)
  public void environmentVariable_notFound() {
    configuration.environmentVariable("RANDOM_KEY");
  }

  @Test
  public void environmentVariable_usesDefault() {
    assertThat(configuration.environmentVariable("RANDOM_KEY", "value4")).isEqualTo("value4");
  }
}