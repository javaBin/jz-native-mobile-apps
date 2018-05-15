package no.schedule.javazone.v3.feedback;


import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.turbomanage.httpclient.BasicHttpClient;

import no.schedule.javazone.v3.BuildConfig;
import no.schedule.javazone.v3.io.model.Feedback;
import no.schedule.javazone.v3.sync.FeedbackApiService;
import no.schedule.javazone.v3.util.ToStringConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class FeedbackApiHelper
{
  private static final String TAG = makeLogTag(FeedbackApiHelper.class);

  private static FeedbackApiService INSTANCE;
  private static Activity activity;
  private FeedbackApiService service;

  public FeedbackApiHelper(Activity activity) {
    String mode = "RELEASE";
    if(BuildConfig.DEBUG) {
      mode = "TEST";
    }

    String endPoint = BuildConfig.SESSION_FEEDBACK_WEB_URI;
    if(mode.equals("TEST")) {
      endPoint = BuildConfig.SESSION_FEEDBACK_WEB_URI_TEST;
    }

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(endPoint)
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(new ToStringConverterFactory())
        .build();

    this.activity = activity;
    service = retrofit.create(FeedbackApiService.class);

  }

  public void setActivity(Activity activity) {
    this.activity = activity;
  }


  public void submitFeedbackToDevNull(String eventId, String sessionId, String voterId, Feedback feedbackBody) {
    service.postSessionFeedback(eventId, sessionId, voterId, feedbackBody).
        enqueue(retrofitCallBack);
  }

  public Callback retrofitCallBack = new Callback() {
    @Override
    public void onResponse(Call call, Response response) {
      Toast.makeText(activity,
          "Thank you for the feedback!",
          Toast.LENGTH_SHORT).show();
      activity.finish();
    }

    @Override
    public void onFailure(Call call, Throwable t) {
      Toast.makeText(activity,
          "Thank you for the feedback!",
          Toast.LENGTH_SHORT).show();
      activity.finish();
    }
  };
}
