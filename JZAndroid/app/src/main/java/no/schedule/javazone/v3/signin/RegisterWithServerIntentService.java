package no.schedule.javazone.v3.signin;

import android.app.IntentService;
import android.content.Intent;

import android.app.IntentService;
import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;
//import no.schedule.javazone.v3.fcm.ServerUtilities;

/**
 * Registers the users account ID and device ID pair with the server.
 */
public class RegisterWithServerIntentService extends IntentService {
  public static final String ACTION_REGISTER = "no.schedule.javazone.v3." +
      "signin.action." + "register";

  public static final String ACTION_UNREGISTER = "no.schedule.javazone.v3." +
      "signin.action." + "unregister";

  public static final String EXTRA_ACCOUNT_ID = "no.schedule.javazone.v3.signin.extra." +
      "account_id";

  public RegisterWithServerIntentService() {
    super("RegisterWithServerIntentService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    /*
    if (intent != null) {
      final String action = intent.getAction();
      final String accountId = intent.getStringExtra(EXTRA_ACCOUNT_ID);
      if (ACTION_REGISTER.equals(action)) {
        ServerUtilities.register(this, FirebaseInstanceId.getInstance().getToken(),
            accountId);
      } else if (ACTION_UNREGISTER.equals(action)) {
        ServerUtilities.unregister(this, FirebaseInstanceId.getInstance().getToken());
      }
    } */
  }
}
