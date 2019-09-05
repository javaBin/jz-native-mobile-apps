package no.schedule.javazone.v3.digitalpass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.navigation.NavigationModel;
import no.schedule.javazone.v3.ui.BaseActivity;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class DigitalPassActivity extends BaseActivity {
    private static final String TAG = makeLogTag(DigitalPassActivity.class);

    private static final String SCREEN_LABEL = "Digital Pass";

    private DigitalPassPagerFragment mDigitalPassPagerFragment;

    private boolean registered = false;

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

         mDigitalPassPagerFragment = (DigitalPassPagerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_content);


        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    public boolean isRegistered(){
        return registered;
    }

    public void setRegistered(boolean registered){
        this.registered = registered;
        //getFragmentManager().findFragmentById()
    }

}
