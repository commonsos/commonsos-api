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

  String environmentVariable(String key) {
    String value = System.getenv(key);
    if (value == null) throw new RuntimeException(String.format("Environment variable %s not defined", key));
    return value;
  }
}
