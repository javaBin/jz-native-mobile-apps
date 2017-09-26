package no.schedule.javazone.v3.sync;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import no.schedule.javazone.v3.BuildConfig;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.util.SettingsUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionApiWebService implements Callback<SessionResult> {
  private static SessionApiWebService INSTANCE;
  private static Context context;
  SessionApiService service;

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

      List<Session> changesList = response.body().sessions;

      ConferenceDataHandler dataHandler = new ConferenceDataHandler(context);
      // TODO apply datahandler to run


      // TODO set preference that you have loaded sessions
      //SettingsUtils.setMarkSessionLoad(context, true);


    } else {
      System.out.println(response.errorBody());
    }
  }

  @Override
  public void onFailure(Call<SessionResult> call, Throwable t) {
    t.printStackTrace();
  }
}
