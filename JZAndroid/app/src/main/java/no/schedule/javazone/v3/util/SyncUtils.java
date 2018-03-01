package no.schedule.javazone.v3.util;


// TODO (b/36980611): move sync util code from SettingsUtils here.

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SyncUtils {
    public static final String SERVER_TIME_OFFSET_PATH = ".info/serverTimeOffset";

    private static final String PREF_SERVER_TIME_OFFSET = "pref_server_time_offset";

    private static final String PREF_SERVER_TIME_OFFSET_SET_AT =
            "pref_server_time_offset_set_at";

    private static final long NEVER_SET = -1L;

    /**
     * Minimum interval for calculating the server time offset.
     */
    public static final long SERVER_TIME_OFFSET_INTERVAL = 3 * 60 * 60 * 1000L; // 3 hours.

    /**
     * Returns the server time offset reported by Firebase RTDB.
     *
     * @param context Context to be used to lookup the {@link android.content.SharedPreferences}.
     */
    public static int getServerTimeOffset(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PREF_SERVER_TIME_OFFSET, 0);
    }

    /**
     * Saves the server time offset reported by Firebase RTDB.
     *
     * @param context The {@link Context}.
     * @param offset  The server time offset.
     */
    public static void setServerTimeOffset(final Context context, int offset) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit()
                .putInt(PREF_SERVER_TIME_OFFSET, offset)
                .putLong(PREF_SERVER_TIME_OFFSET_SET_AT, System.currentTimeMillis())
                .apply();
    }

    /**
     * Returns the last time server time offset was obtained from Firebase RTDB.
     *
     * @param context The {@link Context}.
     */
    public static long getServerTimeOffsetSetAt(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(PREF_SERVER_TIME_OFFSET_SET_AT, NEVER_SET);
    }

    /**
     * Returns true if the server time offset was never set, otherwise false.
     *
     * @param context The {@link Context}.
     */
    public static boolean serverTimeOffsetNeverSet(final Context context) {
        return getServerTimeOffsetSetAt(context) == NEVER_SET;
    }
}
