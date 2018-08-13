package no.schedule.javazone.v3.digitalpass;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.myschedule.MyScheduleDialogFragment;
import no.schedule.javazone.v3.navigation.NavigationModel;
import no.schedule.javazone.v3.schedule.ScheduleView;
import no.schedule.javazone.v3.ui.BaseActivity;

import static no.schedule.javazone.v3.util.LogUtils.LOGD;
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

    private MenuItem mAvatar;

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
        Log.d(TAG, "onCreate: digitalpass");
        setContentView(R.layout.digital_pass_act);
        setFullscreenLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsResumed = true;
        showAnnouncementDialogIfNeeded(getIntent());
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
        showAnnouncementDialogIfNeeded(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mAvatar = menu.findItem(R.id.menu_avatar);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_avatar) {
            showDialogFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // -- Auth

    private void showAvatar() {
        if (mAvatar == null) {
            // Attempt to update avatar image, but avatar view doesn't exist yet
            return;
        }

        mAvatar.setTitle(R.string.description_avatar);
//        Uri photoUrl = AccountUtils.getActiveAccountPhotoUrl(this);
//        if (photoUrl == null) {
//            return;
//        }
//        Glide.with(this).load(photoUrl.toString()).asBitmap()
//                .into(new SimpleTarget<Bitmap>(100, 100) {
//                    @Override
//                    public void onResourceReady(Bitmap resource,
//                                                GlideAnimation glideAnimation) {
//                        if (mAvatar == null) {
//                            return;
//                        }
//                        RoundedBitmapDrawable circularBitmapDrawable =
//                                RoundedBitmapDrawableFactory.create(getResources(), resource);
//                        circularBitmapDrawable.setCircular(true);
//                        mAvatar.setIcon(circularBitmapDrawable);
//                    }
//                });
    }

    void showDialogFragment() {
        FragmentManager fm = getSupportFragmentManager();
        MyScheduleDialogFragment myIODialogFragment = MyScheduleDialogFragment.newInstance();
        myIODialogFragment.show(fm, "my_io_signed_in_dialog_frag");
    }


    // -- Announcement dialog. TODO this may no longer be used

    private void showAnnouncementDialogIfNeeded(Intent intent) {
        if (!mIsResumed) {
            // we are called from onResume, so defer until then
            return;
        }

        final String title = intent.getStringExtra(EXTRA_DIALOG_TITLE);
        final String message = intent.getStringExtra(EXTRA_DIALOG_MESSAGE);

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(message)) {
            final String yes = intent.getStringExtra(EXTRA_DIALOG_YES);
            final String no = intent.getStringExtra(EXTRA_DIALOG_NO);
            final String url = intent.getStringExtra(EXTRA_DIALOG_URL);
            LOGD(TAG, String.format(
                    "showAnnouncementDialog: {\ntitle: %s\nmesg: %s\nyes: %s\nno %s\nurl: %s\n}",
                    title, message, yes, no, url));

            final SpannableString spannable = new SpannableString(message == null ? "" : message);
            Linkify.addLinks(spannable, Linkify.WEB_URLS);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (!TextUtils.isEmpty(title)) {
                builder.setTitle(title);
            }
            builder.setMessage(spannable);
            if (!TextUtils.isEmpty(no)) {
                builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
            }
            if (!TextUtils.isEmpty(yes)) {
                builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                    }
                });
            }
            final AlertDialog dialog = builder.create();
            dialog.show();
            final TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
            if (messageView != null) {
                // makes the embedded links in the text clickable, if there are any
                messageView.setMovementMethod(LinkMovementMethod.getInstance());
            }

            // remove the extras so we don't trigger again
            intent.removeExtra(EXTRA_DIALOG_TITLE);
            intent.removeExtra(EXTRA_DIALOG_MESSAGE);
            intent.removeExtra(EXTRA_DIALOG_YES);
            intent.removeExtra(EXTRA_DIALOG_NO);
            intent.removeExtra(EXTRA_DIALOG_URL);
            setIntent(intent);
        }
    }
}
