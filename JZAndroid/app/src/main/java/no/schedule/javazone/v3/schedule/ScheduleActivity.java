package no.schedule.javazone.v3.schedule;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;

import java.util.Date;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.archframework.PresenterImpl;
import no.schedule.javazone.v3.injection.ModelProvider;
import no.schedule.javazone.v3.model.ScheduleHelper;
import no.schedule.javazone.v3.navigation.NavigationModel;
import no.schedule.javazone.v3.ui.BaseActivity;
import no.schedule.javazone.v3.util.SessionsHelper;
import no.schedule.javazone.v3.util.TimeUtils;

import static no.schedule.javazone.v3.util.LogUtils.LOGD;
import static no.schedule.javazone.v3.util.LogUtils.LOGE;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class ScheduleActivity extends BaseActivity {
    public static final String ARG_CONFERENCE_DAY_INDEX =
            "com.google.samples.apps.iosched.ARG_CONFERENCE_DAY_INDEX";

    /**
     * Int extra used to indicate a specific conference day should shown initially when the screen
     * is launched. Conference days are zero-indexed.
     */
    public static final String EXTRA_CONFERENCE_DAY =
            "com.google.samples.apps.iosched.EXTRA_CONFERENCE_DAY_INDEX";

    // The saved instance state filters
    private static final String STATE_FILTER_TAGS =
            "com.google.samples.apps.iosched.myschedule.STATE_FILTER_TAGS";
    private static final String STATE_CURRENT_URI =
            "com.google.samples.apps.iosched.myschedule.STATE_CURRENT_URI";

    /**
     * Interval that a timer will redraw the UI during the conference, so that time sensitive
     * widgets, like the "Now" and "Ended" indicators can be properly updated.
     */
    private static final long INTERVAL_TO_REDRAW_UI = TimeUtils.MINUTE;

    private static final String SCREEN_LABEL = "Schedule";

    private static final String TAG = makeLogTag(ScheduleActivity.class);
    private final Handler mUpdateUiHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private SchedulePagerFragment mSchedulePagerFragment;
    private ScheduleModel mModel; // TODO decouple this
    private PresenterImpl<ScheduleModel, ScheduleModel.MyScheduleQueryEnum, ScheduleModel.MyScheduleUserActionEnum>
            mPresenter;
    private final Runnable mUpdateUIRunnable = new Runnable() {
        @Override
        public void run() {
            ScheduleActivity activity = ScheduleActivity.this;
            if (activity.isDestroyed()) {
                LOGD(TAG, "Activity is not valid anymore. Stopping UI Updater");
                return;
            }

            LOGD(TAG, "Running MySchedule UI updater (now=" +
                    new Date(TimeUtils.getCurrentTime(activity)) + ")");

            mPresenter.onUserAction(ScheduleModel.MyScheduleUserActionEnum.REDRAW_UI, null);

            if (TimeUtils.isConferenceInProgress(activity)) {
                scheduleNextUiUpdate();
            }
        }
    };


    public static void launchScheduleForConferenceDay(Context context, int day) {
        Intent intent = new Intent(context, ScheduleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_CONFERENCE_DAY, day);
        context.startActivity(intent);
    }

    public static void launchScheduleWithFilterTag(Context context, String tag) {
        Intent intent = new Intent(context, ScheduleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        if (tag != null) {
//            intent.putExtra(EXTRA_FILTER_TAG, tag);
//        }
        context.startActivity(intent);
    }

    @Override
    protected NavigationModel.NavigationItemEnum getSelfNavDrawerItem() {
        return NavigationModel.NavigationItemEnum.SCHEDULE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launchSessionDetailIfRequiredByIntent(getIntent()); // might call finish()
        if (isFinishing()) {
            return;
        }

        setContentView(R.layout.schedule_act);
        setFullscreenLayout();

        mSchedulePagerFragment = (SchedulePagerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.my_content);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        initPresenter();
        if (savedInstanceState == null) {
            // first time through, check for extras
            processIntent(getIntent());
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void processIntent(Intent intent) {
        if (intent.hasExtra(EXTRA_CONFERENCE_DAY)) {
            int day = intent.getIntExtra(EXTRA_CONFERENCE_DAY, -1);
            // clear filters and show the selected day
            mSchedulePagerFragment.scrollToConferenceDay(day);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (TimeUtils.isConferenceInProgress(this)) {
            scheduleNextUiUpdate();
        }
        mModel.addDataObservers();
    }

    @Override
    protected void onPause() {
        mModel.cleanUp();
        super.onPause();
    }

    /**
     * Pre-process the {@code intent} received to open this activity to determine if it was a deep
     * link to a SessionDetail. Typically you wouldn't use this type of logic, but we need to
     * because of the path of session details page on the website is only /schedule and session ids
     * are part of the query parameters ("sid").
     */
    private void launchSessionDetailIfRequiredByIntent(Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getDataString())) {
            String intentDataString = intent.getDataString();
            try {
                Uri dataUri = Uri.parse(intentDataString);

                // Website sends sessionId in query parameter "sid". If present, show
                // SessionDetailActivity
                String sessionId = dataUri.getQueryParameter("sid");
                if (!TextUtils.isEmpty(sessionId)) {
                    LOGD(TAG, "SessionId received from website: " + sessionId);
//                    SessionDetailActivity.startSessionDetailActivity(ScheduleActivity.this,
//                            sessionId);
                    finish();
                } else {
                    LOGD(TAG, "No SessionId received from website");
                }
            } catch (Exception exception) {
                LOGE(TAG, "Data uri existing but wasn't parsable for a session detail deep link");
            }
        }
    }

    private void initPresenter() {
        mModel = ModelProvider.provideMyScheduleModel(
                new ScheduleHelper(this),
                new SessionsHelper(this),
                this);

        final SchedulePagerFragment contentFragment =
                (SchedulePagerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.my_content);

        // Each fragment in the pager adapter is an updatable view that the presenter must know
        mPresenter = new PresenterImpl<>(
                mModel,
                contentFragment.getDayFragments(),
                ScheduleModel.MyScheduleUserActionEnum.values(),
                ScheduleModel.MyScheduleQueryEnum.values());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LOGD(TAG, "onNewIntent, extras " + intent.getExtras());

        launchSessionDetailIfRequiredByIntent(intent); // might call finish()
        if (isFinishing()) {
            return;
        }

        setIntent(intent);
        if (!isFinishing()) {
            processIntent(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean canSwipeRefreshChildScrollUp() {
        final Fragment contentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.my_content);

        if (contentFragment instanceof ScheduleView) {
            return ((ScheduleView) contentFragment).canSwipeRefreshChildScrollUp();
        }

        return false;
    }

    void scheduleNextUiUpdate() {
        // Remove existing UI update runnable, if any
        mUpdateUiHandler.removeCallbacks(mUpdateUIRunnable);
        // Post runnable with delay
        mUpdateUiHandler.postDelayed(mUpdateUIRunnable, INTERVAL_TO_REDRAW_UI);
    }

    private void reloadSchedule() {
        mPresenter.onUserAction(ScheduleModel.MyScheduleUserActionEnum.RELOAD_DATA, null);
        final Fragment contentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.my_content);

    }

    @Override
    protected String getAnalyticsScreenLabel() {
        return SCREEN_LABEL;
    }

    @Override
    protected int getNavigationTitleId() {
        return R.string.title_schedule;
    }
}


//
//public class ScheduleActivity extends AppCompatActivity {
//
//    private TextView mTextMessage;
//
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
//            return false;
//        }
//
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
//
//        mTextMessage = (TextView) findViewById(R.id.message);
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//    }
//
//}
