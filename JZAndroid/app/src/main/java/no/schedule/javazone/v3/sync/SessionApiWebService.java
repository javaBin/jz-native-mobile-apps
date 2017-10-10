package no.schedule.javazone.v3.sync;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import no.schedule.javazone.v3.BuildConfig;
import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.io.JSONHandler;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.util.LogUtils;
import no.schedule.javazone.v3.util.SettingsUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static no.schedule.javazone.v3.util.LogUtils.LOGE;

public class SessionApiWebService implements Callback<SessionResult> {
  private static SessionApiWebService INSTANCE;
  private static Context context;
  SessionApiService service;

  private static final String TAG = LogUtils.makeLogTag(SessionApiWebService.class);


  public SessionApiWebService(Context context) {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(BuildConfig.SLEEPING_PILL_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    this.context = context;
    service = retrofit.create(SessionApiService.class);
  }

  public static SessionApiWebService getInstance(Context context) {
    if (INSTANCE == null) {
      INSTANCE = new SessionApiWebService(context);
    }
    return INSTANCE;
  }

  public void getAllSessions(String sessionSlug) {
    Call<SessionResult> sessions = service.getAllSessions(sessionSlug);
    sessions.enqueue(this);
  }

  @Override
  public void onResponse(Call<SessionResult> call, Response<SessionResult> response) {
    if(response.isSuccessful()) {

      try {
        List<Session> changesList = response.body().sessions;

        ConferenceDataHandler dataHandler = new ConferenceDataHandler(context);

        String bootstrapJson = JSONHandler
            .parseResource(context, R.raw.bootstrap_data);

        dataHandler.applyConferenceData(new String[]{bootstrapJson}, false);


        dataHandler.applyConferenceData(changesList);
        // TODO apply datahandler to run


        // TODO set preference that you have loaded sessions
       // SettingsUtils.setMarkSessionLoad(context, true);
        SettingsUtils.markDataBootstrapDone(context);

        context.getContentResolver().notifyChange(Uri.parse(ScheduleContract.CONTENT_AUTHORITY),
            null, false);

      } catch(IOException ex) {
        LOGE(TAG, "*** ERROR DURING BOOTSTRAP! Problem in bootstrap data?", ex);
        LOGE(TAG,
            "Applying fallback -- marking boostrap as done; sync might fix problem.");
        SettingsUtils.markDataBootstrapDone(context);
      }


    } else {
      System.out.println(response.errorBody());
    }
  }

  @Override
  public void onFailure(Call<SessionResult> call, Throwable t) {
    t.printStackTrace();
  }
}
