package no.schedule.javazone.v3.util;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class ThrottledContentObserver extends ContentObserver {
    Handler mMyHandler;
    Runnable mScheduledRun = null;
    private static final int THROTTLE_DELAY = 1000;
    Callbacks mCallback = null;

    public interface Callbacks {
        public void onThrottledContentObserverFired();
    }

    public ThrottledContentObserver(Callbacks callback) {
        super(null);
        mMyHandler = new Handler();
        mCallback = callback;
    }

    @Override
    public void onChange(boolean selfChange) {
        if (mScheduledRun != null) {
            mMyHandler.removeCallbacks(mScheduledRun);
        } else {
            mScheduledRun = new Runnable() {
                @Override
                public void run() {
                    if (mCallback != null) {
                        mCallback.onThrottledContentObserverFired();
                    }
                }
            };
        }
        mMyHandler.postDelayed(mScheduledRun, THROTTLE_DELAY);
    }

    public void cancelPendingCallback() {
        if (mScheduledRun != null) {
            mMyHandler.removeCallbacks(mScheduledRun);
        }
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        onChange(selfChange);
    }

    public static int getThrottleDelay() {
        return THROTTLE_DELAY;
    }
}
