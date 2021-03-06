package commonsos.domain.message;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.google.firebase.messaging.Message;
import commonsos.Configuration;
import commonsos.domain.auth.User;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Singleton
@Slf4j
public class PushNotificationService {

  @Inject Configuration configuration;

  @Inject
  public void init() {
    try {
      FileInputStream serviceAccount = new FileInputStream(configuration.firebaseCredentialsFile());
      FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();
      FirebaseApp.initializeApp(options);
    }
    catch (Exception e) {
      log.error("Firebase init failed", e);
      throw new RuntimeException(e);
    }
  }

  public void send(User recipient, String message) {
    send(recipient, message, emptyMap());
  }

  public void send(User recipient, String message, Map<String, String> params) {
    if (recipient.getPushNotificationToken() == null) return;
    log.info(String.format("Sending push notification to user: %s, token: %s", recipient.getUsername(), recipient.getPushNotificationToken()));
    send(recipient.getPushNotificationToken(), message, params);
  }

  private void send(String clientToken, String messageBody, Map<String, String> params) {
    AndroidConfig androidConfig = AndroidConfig.builder()
      .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey("personal")
      .setPriority(AndroidConfig.Priority.HIGH)
      .setNotification(AndroidNotification.builder().setTag("personal").build())
      .build();

    ApnsConfig apnsConfig = ApnsConfig.builder()
      .setAps(Aps.builder().setCategory("personal").setThreadId("personal").build())
      .build();

    com.google.firebase.messaging.Message message = messageBuilder().setToken(clientToken)
      .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig)
      .setNotification(new Notification("", messageBody))
      .putAllData(params)
      .build();

    try {
      String response = getInstance().send(message);
      log.info("Successfully sent message: " + response);
    }
    catch (FirebaseMessagingException e) {
      log.warn(String.format("Failed to send push notification to %s", clientToken), e);
    }
  }

  Message.Builder messageBuilder() {
    return Message.builder();
  }

  FirebaseMessaging getInstance() {
    return FirebaseMessaging.getInstance();
  }
}
