package no.schedule.javazone.v3.feedback;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.schedule.javazone.v3.archframework.ModelWithLoaderManager;
import no.schedule.javazone.v3.archframework.QueryEnum;
import no.schedule.javazone.v3.archframework.UserActionEnum;
import no.schedule.javazone.v3.io.model.Feedback;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.sync.FeedbackApiService;
import no.schedule.javazone.v3.util.AnalyticsHelper;

public class SessionFeedbackModel extends
    ModelWithLoaderManager<SessionFeedbackModel.SessionFeedbackQueryEnum, SessionFeedbackModel.SessionFeedbackUserActionEnum> {

  final static String DATA_RATING_INT = "DATA_RATING_INT";

  final static String DATA_SESSION_RELEVANT_ANSWER_INT =
      "DATA_SESSION_RELEVANT_ANSWER_INT";

  final static String DATA_CONTENT_ANSWER_INT = "DATA_CONTENT_ANSWER_INT";

  final static String DATA_SPEAKER_ANSWER_INT = "DATA_SPEAKER_ANSWER_INT";

  final static String DATA_COMMENT_STRING = "DATA_COMMENT_STRING";

  private final Context mContext;

  private Uri mSessionUri;

  private String mTitleString;

  private String mConferenceIdString;

  private String mSpeakersString;

  public SessionFeedbackModel(LoaderManager loaderManager, Uri sessionUri, Context context) {
    super(SessionFeedbackQueryEnum.values(), SessionFeedbackUserActionEnum.values(),
        loaderManager);
    mContext = context;
    mSessionUri = sessionUri;
  }

  String getSessionTitle() {
    return mTitleString;
  }

  String getSessionSpeakers() {
    return mSpeakersString;
  }

  @Override
  public void cleanUp() {
    // Nothing to clean up
  }

  @Override
  public void processUserAction(final SessionFeedbackUserActionEnum action,
                                @Nullable final Bundle args,
                                final UserActionCallback<SessionFeedbackUserActionEnum> callback) {
    switch (action) {
      case SUBMIT:
        int overallAnswer = args.getInt(DATA_RATING_INT);
        int sessionRelevantAnswer = args.getInt(DATA_SESSION_RELEVANT_ANSWER_INT);
        int contentAnswer = args.getInt(DATA_CONTENT_ANSWER_INT);
        int speakerAnswer = args.getInt(DATA_SPEAKER_ANSWER_INT);
        String feedbackComment = args.getString(DATA_COMMENT_STRING);


        Feedback jzFeedback = new Feedback(overallAnswer, sessionRelevantAnswer,
            contentAnswer, speakerAnswer, feedbackComment);

        FeedbackApiHelper.getInstance(mContext).submitFeedbackToDevNull(mConferenceIdString, getSessionId(mSessionUri), generateUniqueVoterId(),jzFeedback);

        // ANALYTICS EVENT: Send session feedback
        // Contains: Session title.  Feedback is NOT included.
        sendAnalyticsEvent("Session", "Feedback", mTitleString);
        callback.onModelUpdated(this, action);
        break;
      default:
        callback.onError(action);
        break;
    }
  }

  public String generateUniqueVoterId() {
    return Settings.Secure.getString(mContext.getContentResolver(),
        Settings.Secure.ANDROID_ID);
  }

  @Override
  public Loader<Cursor> createCursorLoader(final SessionFeedbackQueryEnum query,
                                           final Bundle args) {
    CursorLoader loader = null;
    switch (query) {
      case SESSION:
        loader = getCursorLoaderInstance(mContext, mSessionUri,
            SessionFeedbackQueryEnum.SESSION.getProjection(), null, null, null);
        break;
    }
    return loader;
  }

  @Override
  public boolean readDataFromCursor(final Cursor cursor,
                                    final SessionFeedbackQueryEnum query) {
    if (!cursor.moveToFirst()) {
      return false;
    }

    switch (query) {
      case SESSION:
        mTitleString = cursor.getString(cursor.getColumnIndex(
            ScheduleContract.Sessions.SESSION_TITLE));

        mConferenceIdString = cursor.getString(cursor.getColumnIndex(
            ScheduleContract.Sessions.SESSION_CONFERENCE
        ));

        mSpeakersString = cursor.getString(cursor.getColumnIndex(
            ScheduleContract.Sessions.SESSION_SPEAKER_NAMES));
        return true;
      default:
        return false;

    }
  }

  @VisibleForTesting
  public CursorLoader getCursorLoaderInstance(Context context, Uri uri, String[] projection,
                                              String selection, String[] selectionArgs, String sortOrder) {
    return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
  }

  @VisibleForTesting
  public String getSessionId(Uri uri) {
    return ScheduleContract.Sessions.getSessionId(uri);
  }

  @VisibleForTesting
  public void sendAnalyticsEvent(String category, String action, String label) {
    AnalyticsHelper.sendEvent(category, action, label);
  }

  public enum SessionFeedbackQueryEnum implements QueryEnum {
    SESSION(0, new String[]{
        ScheduleContract.Sessions.SESSION_TITLE,
        ScheduleContract.Sessions.SESSION_CONFERENCE,
        ScheduleContract.Sessions.SESSION_SPEAKER_NAMES});

    private int id;

    private String[] projection;

    SessionFeedbackQueryEnum(int id, String[] projection) {
      this.id = id;
      this.projection = projection;
    }

    @Override
    public int getId() {
      return id;
    }

    @Override
    public String[] getProjection() {
      return projection;
    }

  }

  public enum SessionFeedbackUserActionEnum implements UserActionEnum {
    SUBMIT(1);

    private int id;

    SessionFeedbackUserActionEnum(int id) {
      this.id = id;
    }

    @Override
    public int getId() {
      return id;
    }

  }
}
