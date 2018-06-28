package commonsos;

import javax.inject.Singleton;

@Singleton
public class Configuration {

  public String awsSecurityKey() {
    return environmentVariable("AWS_SECRET_KEY");
  }

  public String awsAccessKey() {
    return environmentVariable("AWS_ACCESS_KEY");
  }

  public String awsS3BucketName() {
    return environmentVariable("AWS_S3_BUCKET_NAME", "commonsos-app");
  }

  public String databaseUrl() {
    return environmentVariable("DATABASE_URL", "jdbc:postgresql://localhost:5432/commonsos");
  }

  public String databaseUsername() {
    return environmentVariable("DATABASE_USERNAME", "commonsos");
  }

  public String databasePassword() {
    return environmentVariable("DATABASE_PASSWORD", "commonsos");
  }

  String environmentVariable(String key) {
    return environmentVariable(key, null);
  }

  String environmentVariable(String key, String defaultValue) {
    String value = System.getenv(key);
    if (value == null && defaultValue != null) return defaultValue;
    if (value == null && defaultValue == null) throw new RuntimeException(String.format("Environment variable %s not defined", key));
    return value;
  }
}
