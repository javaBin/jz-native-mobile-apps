package no.schedule.javazone.v3.sync;

import com.google.gson.JsonElement;

import no.schedule.javazone.v3.io.model.Feedback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FeedbackApiService {
  @Headers( "Content-Type: application/json" )
  @POST("/events/{eventId}/sessions/{sessionId}/feedbacks")
  Call<Object> postSessionFeedback(@Path("eventId") String eventId,
                                   @Path("sessionId") String sessionId,
                                   @Header("Voter-ID") String voterId,
                                   @Body Feedback jzFeedbackBody);

  @Headers("Content-Type: application/json")
  @POST("/events/58b3bfaa-4981-11e5-a151-feff819cdc9f/sessions/58b3c298-4981-11e5-a151-feff819cdc9f/feedbacks")
  Call<String> postSessionFeedbackTest(@Header("Voter-ID") String voterId,
                                       @Body Feedback jzFeedbackBody,
                                       Callback<JsonElement> success);

}
