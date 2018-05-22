package commonsos;

import commonsos.domain.auth.User;
import org.slf4j.MDC;
import spark.Filter;
import spark.Request;
import spark.Response;

import java.util.concurrent.atomic.AtomicLong;

import static commonsos.controller.auth.LoginController.USER_SESSION_ATTRIBUTE_NAME;

public class LogFilter implements Filter {
  public static final String USERNAME_MDC_KEY = "username";
  static final String X_REQUEST_ID = "X-Request-Id";

  AtomicLong requestId = new AtomicLong(0);

  @Override
  public void handle(Request request, Response response) throws Exception {
    MDC.put("requestId", requestId(request));
    MDC.put("sessionId", request.session().id().substring(0, 10));
    String userName = userName(request.session().attribute(USER_SESSION_ATTRIBUTE_NAME));
    MDC.put(USERNAME_MDC_KEY, userName);
    MDC.put("ip", request.ip());
  }

  private String requestId(Request request) {
    return request.headers(X_REQUEST_ID) != null ? request.headers(X_REQUEST_ID) : createRequestId();
  }

  private String userName(User user) {
    return user == null ? "" : user.getUsername();
  }

  private String createRequestId() {
    return Long.toString(requestId.addAndGet(1));
  }
}
