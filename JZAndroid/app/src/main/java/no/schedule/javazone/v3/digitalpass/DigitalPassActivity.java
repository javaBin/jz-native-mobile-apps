package no.schedule.javazone.v3.digitalpass;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.navigation.NavigationModel;
import no.schedule.javazone.v3.schedule.ScheduleView;
import no.schedule.javazone.v3.ui.BaseActivity;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class DigitalPassActivity extends BaseActivity {
    private static final String TAG = makeLogTag(DigitalPassActivity.class);

    private static final String SCREEN_LABEL = "Digital Pass";

    // intent extras used to show an arbitrary message sent via FCM
    public static final String EXTRA_DIALOG_TITLE
            = "no.schedule.javazone.v3.EXTRA_DIALOG_TITLE";
    public static final String EXTRA_DIALOG_MESSAGE
            = "no.schedule.javazone.v3.EXTRA_DIALOG_MESSAGE";
    public static final String EXTRA_DIALOG_YES
            = "no.schedule.javazone.v3.EXTRA_DIALOG_YES";
    public static final String EXTRA_DIALOG_NO
            = "no.schedule.javazone.v3.EXTRA_DIALOG_NO";
    public static final String EXTRA_DIALOG_URL
            = "no.schedule.javazone.v3.EXTRA_DIALOG_URL";

    private boolean mIsResumed;

    /**
     * Reference to Firebase RTDB.
     */
    private DatabaseReference mDatabaseReference;

    /**
     * Listener used to calculate server time offset.
     * TODO (b/36976685): collect server time offset at other places in the app when connecting to RTDB.
     */
    private ValueEventListener mValueEventListener;


    // -- BaseActivity overrides

    @Override
    protected NavigationModel.NavigationItemEnum getSelfNavDrawerItem() {
        return NavigationModel.NavigationItemEnum.DIGITAL_PASS;
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

    @Override
    protected String getAnalyticsScreenLabel() {
        return SCREEN_LABEL;
    }

    @Override
    protected int getNavigationTitleId() {
        return R.string.title_my_schedule;
    }

    // -- Lifecycle callbacks

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.digital_pass_act);
        setFullscreenLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsResumed = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

}
