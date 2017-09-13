package no.schedule.javazone.v3.util;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.UUID;

import static no.schedule.javazone.v3.util.LogUtils.LOGD;
import static no.schedule.javazone.v3.util.LogUtils.LOGE;
import static no.schedule.javazone.v3.util.LogUtils.LOGI;
import static no.schedule.javazone.v3.util.LogUtils.LOGV;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class AccountUtils {
  private static final String TAG = makeLogTag(AccountUtils.class);

  private static final String PREF_ACTIVE_ACCOUNT = "no.schedule.javazone.v3";
  private static final String DUMMY_ACCOUNT_NAME = "no.schedule.javazone.v3";
  private static final String DUMMY_ACCOUNT_TYPE = "no.schedule.javazone.v3";
  private static final String DUMMY_AUTH_TOKEN = "authtoken";
  private static android.accounts.Account mAccount;

  // these names are are prefixes; the account is appended to them
  private static final String PREFIX_PREF_AUTH_TOKEN = "auth_token_";
  private static final String PREFIX_PREF_PLUS_PROFILE_ID = "plus_profile_id_";
  private static final String PREFIX_PREF_GCM_KEY = "gcm_key_";

  /**
   * Used for accessing the account id stored in {@link SharedPreferences}.
   */
  private static final String PREF_ACTIVE_ACCOUNT_ID =
      "pref_active_account_id" + Constants.CONFERENCE_YEAR_PREF_POSTFIX;

  private static SharedPreferences getSharedPreferences(final Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  public static boolean hasActiveAccount(final Context context) {
    return !TextUtils.isEmpty(getActiveAccountName(context));
  }

  /**
   * Return the accountName the app is using as the active Google Account.
   *
   * @param context Context used to lookup {@link SharedPreferences} the value is stored with.
   */
  public static String getActiveAccountName(final Context context) {
    SharedPreferences sp = getSharedPreferences(context);
    return sp.getString(PREF_ACTIVE_ACCOUNT, null);
  }

  public static Account getActiveAccount(final Context context) {
    String account = getActiveAccountName(context);
    if (account != null) {
      return new Account(account, PREF_ACTIVE_ACCOUNT);
    } else {
      return null;
    }
  }


  public static boolean setActiveAccount(final Context context, final String accountName) {
    LOGD(TAG, "Set active account to: " + accountName);
    SharedPreferences sp = getSharedPreferences(context);
    sp.edit().putString(PREF_ACTIVE_ACCOUNT, accountName).apply();
    return true;
  }



  public static boolean setActiveAccount(final Context context) {
    SharedPreferences sp = getSharedPreferences(context);
    sp.edit().putString(PREF_ACTIVE_ACCOUNT, DUMMY_ACCOUNT_NAME).apply();
    return true;
  }

  private static String makeAccountSpecificPrefKey(Context ctx, String prefix) {
    return hasActiveAccount(ctx) ? makeAccountSpecificPrefKey(getActiveAccountName(ctx),
        prefix) : null;
  }

  private static String makeAccountSpecificPrefKey(String accountName, String prefix) {
    return prefix + accountName;
  }

  /**
   * Returns the display name associated with the active account.
   *
   * @param context Context used to lookup {@link SharedPreferences}
   */
  public static String getActiveAccountId(final Context context) {
    SharedPreferences sp = getSharedPreferences(context);
    return sp.getString(PREF_ACTIVE_ACCOUNT_ID, "");
  }

  public static String getAuthToken(final Context context) {
    SharedPreferences sp = getSharedPreferences(context);
    return hasActiveAccount(context) ?
        sp.getString(makeAccountSpecificPrefKey(context, PREFIX_PREF_AUTH_TOKEN), null) : null;
  }

  public static void setAuthToken(final Context context, final String accountName, final String authToken) {
    LOGI(TAG, "Auth token of length "
        + (TextUtils.isEmpty(authToken) ? 0 : authToken.length()) + " for "
        + accountName);
    SharedPreferences sp = getSharedPreferences(context);
    sp.edit().putString(makeAccountSpecificPrefKey(accountName, PREFIX_PREF_AUTH_TOKEN),
        authToken).apply();
    LOGV(TAG, "Auth Token: " + authToken);
  }

  public static boolean hasToken(final Context context, final String accountName) {
    SharedPreferences sp = getSharedPreferences(context);
    return !TextUtils.isEmpty(sp.getString(makeAccountSpecificPrefKey(accountName,
        PREFIX_PREF_AUTH_TOKEN), null));
  }

  public static void setGcmKey(final Context context, final String accountName, final String gcmKey) {
    SharedPreferences sp = getSharedPreferences(context);
    sp.edit().putString(makeAccountSpecificPrefKey(accountName, PREFIX_PREF_GCM_KEY),
        gcmKey).apply();
    LOGD(TAG, "GCM key of account " + accountName + " set to: " + sanitizeGcmKey(gcmKey));
  }

  public static String getGcmKey(final Context context, final String accountName) {
    SharedPreferences sp = getSharedPreferences(context);
    String gcmKey = sp.getString(makeAccountSpecificPrefKey(accountName,
        PREFIX_PREF_GCM_KEY), null);

    // if there is no current GCM key, generate a new random one
    if (TextUtils.isEmpty(gcmKey)) {
      gcmKey = UUID.randomUUID().toString();
      LOGD(TAG, "No GCM key on account " + accountName + ". Generating random one: "
          + sanitizeGcmKey(gcmKey));
      setGcmKey(context, accountName, gcmKey);
    }

    return gcmKey;
  }

  public static String sanitizeGcmKey(String key) {
    if (key == null) {
      return "(null)";
    } else if (key.length() > 8) {
      return key.substring(0, 4) + "........" + key.substring(key.length() - 4);
    } else {
      return "........";
    }
  }
}

