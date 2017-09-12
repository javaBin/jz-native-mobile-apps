package no.schedule.javazone.v3.util;

import android.support.annotation.Nullable;

/**
 * Utility methods for checking preconditions.
 */
public final class PreconditionUtils {

  private PreconditionUtils() {
    //no instance
  }

  /**
   * Check that an object reference is not null
   *
   * @param reference the object to check
   * @return the argument, to support inline calls to this method
   * @throws NullPointerException if the argument is null
   */
  public static <T> T checkNotNull(T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }

  /**
   * Check the truth of a boolean expression
   *
   * @param expression   a boolean expression
   * @param errorMessage the exception message to use if the check fails; will be converted to a
   *                     string using String.valueOf(Object)
   * @throws IllegalStateException if the expression is false
   */
  public static void checkState(boolean expression, @Nullable Object errorMessage) {
    if (!expression) {
      throw new IllegalStateException(String.valueOf(errorMessage));
    }
  }

}
