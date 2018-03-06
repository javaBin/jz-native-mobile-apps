package no.schedule.javazone.v3.util;

import android.content.ContentProvider;
import android.net.Uri;
import android.text.format.Time;

import com.google.api.client.util.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.regex.Pattern;


public class ParserUtils {
  /** Used to sanitize a string to be {@link Uri} safe. */
  private static final Pattern sSanitizePattern = Pattern.compile("[^a-z0-9-_]");

  /**
   * Sanitize the given string to be {@link Uri} safe for building
   * {@link ContentProvider} paths.
   */
  public static String sanitizeId(String input) {
    if (input == null) {
      return null;
    }
    //noinspection DefaultLocale
    return sSanitizePattern.matcher(input.replace("+", "plus").toLowerCase()).replaceAll("");
  }

  /**
   * Parse the given string as a RFC 3339 timestamp, returning the value as
   * milliseconds since the epoch.
   */
  public static long parseTime(String timestamp) {
    DateTime dateTimeNew = new DateTime(timestamp);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    String datetimeString = dateTimeNew.toString();
    long timeParsed = 0;
    try {
      timeParsed = simpleDateFormat.parse(datetimeString).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return timeParsed;
  }

  public static String joinStrings(String connector, ArrayList<String> strings, StringBuilder recycle) {
    if (strings.size() <= 0) {
      return "";
    }
    if (recycle == null) {
      recycle = new StringBuilder();
    } else {
      recycle.setLength(0);
    }
    recycle.append(strings.get(0));
    for (int i = 1; i < strings.size(); i++) {
      recycle.append(connector);
      recycle.append(strings.get(i));
    }
    return recycle.toString();
  }
}

