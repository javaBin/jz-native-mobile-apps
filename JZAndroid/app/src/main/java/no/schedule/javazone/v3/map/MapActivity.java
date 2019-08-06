package no.schedule.javazone.v3.map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.navigation.NavigationModel;
import no.schedule.javazone.v3.ui.BaseActivity;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class MapActivity extends BaseActivity {
    private static final String TAG = makeLogTag(MapActivity.class);

    private static final String SCREEN_LABEL = "Map";
    public static final int MARKER_REQUEST = 0;

    // -- BaseActivity overrides

    @Override
    protected NavigationModel.NavigationItemEnum getSelfNavDrawerItem() {
        return NavigationModel.NavigationItemEnum.MAP;
    }

    // -- Lifecycle callbacks

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: map");
        setContentView(R.layout.map_act);
        setFullscreenLayout();
    }
//
//    @Override
//    public void startActivity(Intent intent, int requestCode) {
//        if(requestCode == MARKER_REQUEST)
//
//    }
}
