package no.schedule.javazone.v3.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.util.AnalyticsHelper;

import static no.schedule.javazone.v3.util.LogUtils.LOGD;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public abstract class BaseActivity extends AppCompatActivity implements
    SharedPreferences.OnSharedPreferenceChangeListener {

  private static final String TAG = makeLogTag(BaseActivity.class);
  // Navigation drawer
  //private AppNavigationView mAppNavigationView;
  // Toolbar
  private Toolbar mToolbar;
  private TextView mToolbarTitle;
  // SwipeRefreshLayout allows the user to swipe the screen down to trigger a manual refresh
  private SwipeRefreshLayout mSwipeRefreshLayout;

  // handle to our sync observer (that notifies us about changes in our sync state)
  private Object mSyncObserverHandle;

  /**
   * This utility method handles Up navigation intents by searching for a parent activity and
   * navigating there if defined. When using this for an activity make sure to define both the
   * native parentActivity as well as the AppCompat one when supporting API levels less than 16.
   * when the activity has a single parent activity. If the activity doesn't have a single parent
   * activity then don't define one and this method will use back button functionality. If "Up"
   * functionality is still desired for activities without parents then use {@code
   * syntheticParentActivity} to define one dynamically.
   * <p/>
   * Note: Up navigation intents are represented by a back arrow in the top left of the Toolbar in
   * Material Design guidelines.
   *
   * @param currentActivity         Activity in use when navigate Up action occurred.
   * @param syntheticParentActivity Parent activity to use when one is not already configured.
   */
  public static void navigateUpOrBack(Activity currentActivity,
                                      Class<? extends Activity> syntheticParentActivity) {
    // Retrieve parent activity from AndroidManifest.
    Intent intent = NavUtils.getParentActivityIntent(currentActivity);

    // Synthesize the parent activity when a natural one doesn't exist.
    if (intent == null && syntheticParentActivity != null) {
      try {
        intent = NavUtils.getParentActivityIntent(currentActivity, syntheticParentActivity);
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
    }

    if (intent == null) {
      // No parent defined in manifest. This indicates the activity may be used by
      // in multiple flows throughout the app and doesn't have a strict parent. In
      // this case the navigation up button should act in the same manner as the
      // back button. This will result in users being forwarded back to other
      // applications if currentActivity was invoked from another application.
      currentActivity.onBackPressed();
    } else {
      if (NavUtils.shouldUpRecreateTask(currentActivity, intent)) {
        // Need to synthesize a backstack since currentActivity was probably invoked by a
        // different app. The preserves the "Up" functionality within the app according to
        // the activity hierarchy defined in AndroidManifest.xml via parentActivity
        // attributes.
        TaskStackBuilder builder = TaskStackBuilder.create(currentActivity);
        builder.addNextIntentWithParentStack(intent);
        builder.startActivities();
      } else {
        // Navigate normally to the manifest defined "Up" activity.
        NavUtils.navigateUpTo(currentActivity, intent);
      }
    }
  }

  /**
   * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
   */
  public static Bundle intentToFragmentArguments(Intent intent) {
    Bundle arguments = new Bundle();
    if (intent == null) {
      return arguments;
    }

    final Uri data = intent.getData();
    if (data != null) {
      arguments.putParcelable("_uri", data);
    }

    final Bundle extras = intent.getExtras();
    if (extras != null) {
      arguments.putAll(intent.getExtras());
    }

    return arguments;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    sp.registerOnSharedPreferenceChangeListener(this);

    ActionBar ab = getSupportActionBar();
    if (ab != null) {
      ab.setDisplayHomeAsUpEnabled(true);
    }

    String screenLabel = getAnalyticsScreenLabel();
    if (screenLabel != null) {
      AnalyticsHelper.sendScreenView(screenLabel, this);
    }
  }

  private void trySetupSwipeRefresh() {
  }


  @Override
  public void setContentView(int layoutResID) {
    super.setContentView(layoutResID);
    getToolbar();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);

    trySetupSwipeRefresh();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }


  protected void requestDataRefresh() {
    LOGD(TAG, "Requesting manual data refresh.");
   // SyncHelper.requestManualSync();
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Perform one-time bootstrap setup, if needed
    //DataBootstrapService.startDataBootstrapIfNecessary(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mSyncObserverHandle != null) {
      ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
      mSyncObserverHandle = null;
    }
  }

  @Override
  protected void onDestroy() {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
    sp.unregisterOnSharedPreferenceChangeListener(this);
    super.onDestroy();
  }

  public Toolbar getToolbar() {
    if (mToolbar == null) {
      mToolbar = (Toolbar) findViewById(R.id.toolbar);
      if (mToolbar != null) {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationContentDescription(R.string.navdrawer_description_a11y);

        // We use our own toolbar title, so hide the default one
        getSupportActionBar().setDisplayShowTitleEnabled(false);
      }
    }
    return mToolbar;
  }

  /**
   * @param clickListener The {@link android.view.View.OnClickListener} for the navigation icon of
   *                      the toolbar.
   */
  protected void setToolbarAsUp(View.OnClickListener clickListener) {
    // Initialise the toolbar
    getToolbar();
    if (mToolbar != null) {
      mToolbar.setNavigationOnClickListener(clickListener);
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
  }

  protected void onRefreshingStateChanged(boolean refreshing) {
    if (mSwipeRefreshLayout != null) {
      mSwipeRefreshLayout.setRefreshing(refreshing);
    }
  }

  protected String getAnalyticsScreenLabel() {
    return null;
  }

  protected int getNavigationTitleId() {
    return 0;
  }

  protected void setFullscreenLayout() {
    View decor = getWindow().getDecorView();
    int flags = decor.getSystemUiVisibility();
    flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    decor.setSystemUiVisibility(flags);
  }


}