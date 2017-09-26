package no.schedule.javazone.v3.provider;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;

public class ScheduleContractHelper {
  public static final String QUERY_PARAMETER_DISTINCT = "distinct";
  private static final String QUERY_PARAMETER_OVERRIDE_ACCOUNT_NAME = "overrideAccountName";
  private static final String QUERY_PARAMETER_CALLER_IS_SERVICE_API = "callerIsServiceApi";
  private static final String QUERY_PARAMETER_ACCOUNT_UPDATE_ALLOWED = "accountUpdateAllowed";

  public static boolean isQueryDistinct(Uri uri){
    return !TextUtils.isEmpty(uri.getQueryParameter(QUERY_PARAMETER_DISTINCT));
  }

  public static boolean isUriCalledFromServiceApi(Uri uri) {
    return uri.getBooleanQueryParameter(QUERY_PARAMETER_CALLER_IS_SERVICE_API, false);
  }

  public static Uri setUriAsCalledFromServiceApi(Uri uri) {
    return uri.buildUpon().appendQueryParameter(QUERY_PARAMETER_CALLER_IS_SERVICE_API, "true")
        .build();
  }

  public static String formatQueryDistinctParameter(String parameter){
    return ScheduleContractHelper.QUERY_PARAMETER_DISTINCT + " " + parameter;
  }

  public static String getOverrideAccountName(Uri uri) {
    return uri.getQueryParameter(QUERY_PARAMETER_OVERRIDE_ACCOUNT_NAME);
  }
  public static Uri addOverrideAccountName(Uri uri, String accountName) {
    return uri.buildUpon().appendQueryParameter(
        QUERY_PARAMETER_OVERRIDE_ACCOUNT_NAME, accountName).build();
  }

  public static boolean isAccountUpdateAllowed(Uri uri) {
    String value = uri.getQueryParameter(QUERY_PARAMETER_ACCOUNT_UPDATE_ALLOWED);
    return value != null && "true".equals(value);
  }

  public static Uri addOverrideAccountUpdateAllowed(Uri uri) {
    return uri.buildUpon().appendQueryParameter(
        QUERY_PARAMETER_ACCOUNT_UPDATE_ALLOWED, "true").build();
  }
}
