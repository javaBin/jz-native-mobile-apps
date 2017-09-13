package no.schedule.javazone.v3.sync;

import java.util.List;

import no.schedule.javazone.v3.io.model.Session;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SessionApiService {
  @GET("public/allSessions/{sessionSlug}")
  Call<SessionResult> getAllSessions(@Path("sessionSlug") String sessionSlug);
}
