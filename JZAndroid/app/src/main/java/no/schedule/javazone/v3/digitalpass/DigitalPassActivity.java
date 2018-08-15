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

    // -- BaseActivity overrides

    @Override
    protected NavigationModel.NavigationItemEnum getSelfNavDrawerItem() {
        return NavigationModel.NavigationItemEnum.DIGITAL_PASS;
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
