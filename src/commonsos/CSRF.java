package commonsos;

import spark.Request;
import spark.Response;

import static java.util.UUID.randomUUID;

public class CSRF {

  public static final String CSRF_TOKEN_COOKIE_NAME = "XSRF-TOKEN";
  public static final String CSRF_TOKEN_SESSION_ATTRIBUTE_NAME = "XSRF-TOKEN";

  public void setToken(Request request, Response response) {
    String csrfToken = generateToken();
    request.session().attribute(CSRF_TOKEN_SESSION_ATTRIBUTE_NAME, csrfToken);
    response.cookie("/", CSRF_TOKEN_COOKIE_NAME, csrfToken, -1, false);
  }

  String generateToken() {
    return randomUUID().toString();
  }
}
