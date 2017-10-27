package no.schedule.javazone.v3.io.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import no.schedule.javazone.v3.R;
import no.schedule.javazone.v3.io.JSONHandler;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.io.model.Speaker;
import no.schedule.javazone.v3.io.model.Tag;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.provider.ScheduleContractHelper;
import no.schedule.javazone.v3.provider.ScheduleDatabase;
import no.schedule.javazone.v3.util.TimeUtils;

import static no.schedule.javazone.v3.util.LogUtils.LOGD;
import static no.schedule.javazone.v3.util.LogUtils.LOGE;
import static no.schedule.javazone.v3.util.LogUtils.LOGW;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class SessionsHandler extends JSONHandler {
  private static final String TAG = makeLogTag(SessionsHandler.class);
  private HashMap<String, Session> mSessions = new HashMap<>();
  private HashMap<String, Speaker> mSpeakerMap = null;

  private int mDefaultSessionColor;

  public SessionsHandler(Context context) {
    super(context);
    mDefaultSessionColor = ContextCompat.getColor(mContext, R.color.default_session_color);
  }

  @Override
  public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
    Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Sessions.CONTENT_URI);
    // build a map of session to session import hashcode so we know what to update,
    // what to insert, and what to delete
    HashMap<String, String> sessionHashCodes = loadSessionHashCodes();
    boolean incrementalUpdate = (sessionHashCodes != null) && (sessionHashCodes.size() > 0);

    // set of sessions that we want to keep after the sync
    HashSet<String> sessionsToKeep = new HashSet<>();

    if (incrementalUpdate) {
      LOGD(TAG, "Doing incremental update for sessions.");
    } else {
      LOGD(TAG, "Doing full (non-incremental) update for sessions.");
      list.add(ContentProviderOperation.newDelete(uri).build());
    }

    int updatedSessions = 0;
    for (Session session : mSessions.values()) {

      // compute the incoming session's hashcode to figure out if we need to update
      String hashCode = session.getImportHashCode();
      sessionsToKeep.add(session.id);

      // add session, if necessary
      if (!incrementalUpdate || !sessionHashCodes.containsKey(session.id) ||
          !sessionHashCodes.get(session.id).equals(hashCode)) {
        ++updatedSessions;
        boolean isNew = !incrementalUpdate || !sessionHashCodes.containsKey(session.id);
        buildSession(isNew, session, list);

        // add relationships to speakers and track
        buildSessionSpeakerMapping(session, list);
        buildTagsMapping(session, list);
      }
    }

    int deletedSessions = 0;
    if (incrementalUpdate) {
      for (String sessionId : sessionHashCodes.keySet()) {
        if (!sessionsToKeep.contains(sessionId)) {
          buildDeleteOperation(sessionId, list);
          ++deletedSessions;
        }
      }
    }

    LOGD(TAG, "Sessions: " + (incrementalUpdate ? "INCREMENTAL" : "FULL") + " update. " +
        updatedSessions + " to update, " + deletedSessions + " to delete. New total: " +
        mSessions.size());
  }

  private void buildDeleteOperation(String sessionId, List<ContentProviderOperation> list) {
    Uri sessionUri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Sessions.buildSessionUri(sessionId));
    list.add(ContentProviderOperation.newDelete(sessionUri).build());
  }

  private HashMap<String, String> loadSessionHashCodes() {
    Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Sessions.CONTENT_URI);
    LOGD(TAG, "Loading session hashcodes for session import optimization.");
    Cursor cursor = null;
    try {
      cursor = mContext.getContentResolver().query(uri, SessionHashcodeQuery.PROJECTION,
          null, null, null);
      if (cursor == null || cursor.getCount() < 1) {
        LOGW(TAG, "Warning: failed to load session hashcodes. Not optimizing session import.");
        return null;
      }
      HashMap<String, String> hashcodeMap = new HashMap<>();
      if (cursor.moveToFirst()) {
        do {
          String sessionId = cursor.getString(SessionHashcodeQuery.SESSION_ID);
          String hashcode = cursor.getString(SessionHashcodeQuery.SESSION_IMPORT_HASHCODE);
          hashcodeMap.put(sessionId, hashcode == null ? "" : hashcode);
        } while (cursor.moveToNext());
      }
      LOGD(TAG, "Session hashcodes loaded for " + hashcodeMap.size() + " sessions.");
      return hashcodeMap;
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  StringBuilder mStringBuilder = new StringBuilder();

  private void buildSession(boolean isInsert,
                            Session session, ArrayList<ContentProviderOperation> list) {
    ContentProviderOperation.Builder builder;
    Uri allSessionsUri = ScheduleContractHelper
        .setUriAsCalledFromServiceApi(ScheduleContract.Sessions.CONTENT_URI);
    Uri thisSessionUri = ScheduleContractHelper
        .setUriAsCalledFromServiceApi(ScheduleContract.Sessions.buildSessionUri(
            session.id));

    if (isInsert) {
      builder = ContentProviderOperation.newInsert(allSessionsUri);
    } else {
      builder = ContentProviderOperation.newUpdate(thisSessionUri);
    }

    String speakerNames = "";
    if (mSpeakerMap != null) {
      // build human-readable list of speakers
      mStringBuilder.setLength(0);
      if (session.speakers != null) {
        for (int i = 0; i < session.speakers.length; ++i) {
          if (mSpeakerMap.containsKey(session.speakers[i])) {
            mStringBuilder
                .append(i == 0 ? "" :
                    i == session.speakers.length - 1 ? " and " : ", ")
                .append(mSpeakerMap.get(session.speakers[i]).name.trim());
          } else {
            LOGW(TAG, "Unknown speaker ID " + session.speakers[i] + " in session " +
                session.id);
          }
        }
      }
      speakerNames = mStringBuilder.toString();
    } else {
      LOGE(TAG, "Can't build speaker names -- speaker map is null.");
    }

    builder.withValue(ScheduleContract.SyncColumns.UPDATED, System.currentTimeMillis())
        .withValue(ScheduleContract.Sessions.SESSION_ID, session.id)
        .withValue(ScheduleContract.Sessions.SESSION_LEVEL, session.level)
        .withValue(ScheduleContract.Sessions.SESSION_TITLE, session.title)
        .withValue(ScheduleContract.Sessions.SESSION_ABSTRACT, session.description)
        .withValue(ScheduleContract.Sessions.SESSION_INTENDED_AUDIENCE, session.intendedAudience)
        .withValue(ScheduleContract.Sessions.SESSION_START, TimeUtils.timestampToMillis(session.startTime, 0))
        .withValue(ScheduleContract.Sessions.SESSION_END, TimeUtils.timestampToMillis(session.endTime, 0))
        .withValue(ScheduleContract.Sessions.SESSION_TAGS, session.makeTagsList())
        // Note: we store this comma-separated list of tags IN ADDITION
        // to storing the tags in proper relational format (in the sessions_tags
        // relationship table). This is because when querying for sessions,
        // we don't want to incur the performance penalty of having to do a
        // subquery for every record to figure out the list of tags of each session.
        .withValue(ScheduleContract.Sessions.SESSION_SPEAKER_NAMES, speakerNames)
        .withValue(ScheduleContract.Sessions.SESSION_VIMEO_URL, session.video)
        .withValue(ScheduleContract.Sessions.ROOM_ID, session.room)
        .withValue(ScheduleContract.Sessions.SESSION_IMPORT_HASHCODE,
            session.getImportHashCode());
    list.add(builder.build());
  }

  private void buildSessionSpeakerMapping(Session session,
                                          ArrayList<ContentProviderOperation> list) {
    final Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Sessions.buildSpeakersDirUri(session.id));

    // delete any existing relationship between this session and speakers
    list.add(ContentProviderOperation.newDelete(uri).build());

    // add relationship records to indicate the speakers for this session
    if (session.speakers != null) {
      for (Speaker speakers : session.speakers) {
        list.add(ContentProviderOperation.newInsert(uri)
            .withValue(ScheduleDatabase.SessionsSpeakers.SESSION_ID, session.id)
            .withValue(ScheduleDatabase.SessionsSpeakers.SPEAKER_ID, speakers.id)
            .build());
      }
    }
  }

  private void buildTagsMapping(Session session, ArrayList<ContentProviderOperation> list) {
    final Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Sessions.buildTagsDirUri(session.id));

    // delete any existing mappings
    list.add(ContentProviderOperation.newDelete(uri).build());

    // add a mapping (a session+tag tuple) for each tag in the session
    if (session.tags != null) {
      for (String tagName : session.tags) {
        list.add(ContentProviderOperation.newInsert(uri)
            .withValue(ScheduleDatabase.SessionsTags.SESSION_ID, session.id)
            .withValue(ScheduleDatabase.SessionsTags.TAG_ID, tagName)
            .build());
      }
    }
  }

  public void setSpeakerMap(HashMap<String, Speaker> speakerMap) {
    mSpeakerMap = speakerMap;
  }

  private interface SessionHashcodeQuery {
    String[] PROJECTION = {
        BaseColumns._ID,
        ScheduleContract.Sessions.SESSION_ID,
        ScheduleContract.Sessions.SESSION_IMPORT_HASHCODE
    };
    int _ID = 0;
    int SESSION_ID = 1;
    int SESSION_IMPORT_HASHCODE = 2;
  };

  @Override
  public void process(@NonNull List<Session> sessions) {
    for(Session session: sessions) {
      mSessions.put(session.id, session);
    }
  }


  @Override
  public void process(@NonNull Gson gson, @NonNull JsonElement element) {
    for (Session session : gson.fromJson(element, Session[].class)) {
      mSessions.put(session.id, session);
    }
  }
}
