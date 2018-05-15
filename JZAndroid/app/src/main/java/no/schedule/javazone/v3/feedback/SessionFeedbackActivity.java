package no.schedule.javazone.v3.feedback;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.ui.BaseActivity;
import no.schedule.javazone.v3.ui.ScheduleActivity;
import no.schedule.javazone.v3.util.Constants;

import static no.schedule.javazone.v3.util.LogUtils.LOGE;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class SessionFeedbackActivity extends BaseActivity {
  private final static String TAG = makeLogTag(SessionFeedbackActivity.class);

  private Uri mSessionUri = null;

  public static void launchFeedback(Context context, String sessionId) {
    Uri uri = ScheduleContract.Sessions.buildSessionUri(sessionId);
    Intent intent = new Intent(Intent.ACTION_VIEW, uri,
        context, SessionFeedbackActivity.class);
    intent.putExtra(Constants.SESSION_ID, sessionId);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.session_feedback_act);

    mSessionUri = getIntent().getData();

    if (mSessionUri == null) {
      LOGE(TAG, "SessionFeedbackActivity started with null data URI!");
      finish();
    }

    setToolbarAsUp(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        NavUtils.navigateUpTo(SessionFeedbackActivity.this,
            getParentActivityIntent());
      }
    });
  }

  protected void setFullscreenLayout() {
    View decor = getWindow().getDecorView();
    int flags = decor.getSystemUiVisibility();
    flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
    decor.setSystemUiVisibility(flags);
  }

  @Override
  public Intent getParentActivityIntent() {
    // Up to this session's track details, or Home if no track is available
    if (mSessionUri != null) {
      return new Intent(Intent.ACTION_VIEW, mSessionUri);
    } else {
      return new Intent(this, ScheduleActivity.class);
    }
  }

  public Uri getSessionUri() {
    return mSessionUri;
  }
}
