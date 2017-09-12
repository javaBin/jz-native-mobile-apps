package no.schedule.javazone.v3.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class ScheduleActivity extends BaseActivity {
  private static final String SCREEN_LABEL = "Schedule";

  private static final String TAG = makeLogTag(ScheduleActivity.class);
  private final Handler mUpdateUiHandler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

}
