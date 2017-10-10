package no.schedule.javazone.v3.io.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import no.schedule.javazone.v3.io.JSONHandler;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.io.model.Speaker;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.provider.ScheduleContractHelper;

import static no.schedule.javazone.v3.util.LogUtils.LOGD;
import static no.schedule.javazone.v3.util.LogUtils.LOGE;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class SpeakersHandler extends JSONHandler {
  private static final String TAG = makeLogTag(SpeakersHandler.class);
  private HashMap<String, Speaker> mSpeakers = new HashMap<>();

  public SpeakersHandler(Context context) {
    super(context);
  }

  @Override
  public void process(@NonNull Gson gson, @NonNull JsonElement element) {
    for (Speaker speaker : gson.fromJson(element, Speaker[].class)) {
      mSpeakers.put(speaker.id, speaker);
    }
  }

  @Override
  public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
    Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Speakers.CONTENT_URI);
    HashMap<String, String> speakerHashcodes = loadSpeakerHashcodes();
    HashSet<String> speakersToKeep = new HashSet<>();
    boolean isIncrementalUpdate = speakerHashcodes != null && speakerHashcodes.size() > 0;

    if (isIncrementalUpdate) {
      LOGD(TAG, "Doing incremental update for speakers.");
    } else {
      LOGD(TAG, "Doing FULL (non incremental) update for speakers.");
      list.add(ContentProviderOperation.newDelete(uri).build());
    }

    int updatedSpeakers = 0;
    for (Speaker speaker : mSpeakers.values()) {
      String hashCode = speaker.getImportHashcode();
      speakersToKeep.add(speaker.id);

      // add speaker, if necessary
      if (!isIncrementalUpdate || !speakerHashcodes.containsKey(speaker.id) ||
          !speakerHashcodes.get(speaker.id).equals(hashCode)) {
        ++updatedSpeakers;
        boolean isNew = !isIncrementalUpdate || !speakerHashcodes.containsKey(speaker.id);
        buildSpeaker(isNew, speaker, list);
      }
    }

    int deletedSpeakers = 0;
    if (isIncrementalUpdate) {
      for (String speakerId : speakerHashcodes.keySet()) {
        if (!speakersToKeep.contains(speakerId)) {
          buildDeleteOperation(speakerId, list);
          ++deletedSpeakers;
        }
      }
    }

    LOGD(TAG, "Speakers: " + (isIncrementalUpdate ? "INCREMENTAL" : "FULL") + " update. " +
        updatedSpeakers + " to update, " + deletedSpeakers + " to delete. New total: " +
        mSpeakers.size());
  }

  @Override
  public void process(@NonNull List<Session> sessions) {

  }

  private void buildSpeaker(boolean isInsert, Speaker speaker,
                            ArrayList<ContentProviderOperation> list) {
    Uri allSpeakersUri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Speakers.CONTENT_URI);
    Uri thisSpeakerUri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Speakers.buildSpeakerUri(speaker.id));

    ContentProviderOperation.Builder builder;
    if (isInsert) {
      builder = ContentProviderOperation.newInsert(allSpeakersUri);
    } else {
      builder = ContentProviderOperation.newUpdate(thisSpeakerUri);
    }

    list.add(builder.withValue(ScheduleContract.SyncColumns.UPDATED, System.currentTimeMillis())
        .withValue(ScheduleContract.Speakers.SPEAKER_ID, speaker.id)
        .withValue(ScheduleContract.Speakers.SPEAKER_NAME, speaker.name)
        .withValue(ScheduleContract.Speakers.SPEAKER_ABSTRACT, speaker.bio)
        .withValue(ScheduleContract.Speakers.SPEAKER_TWITTER_URL, speaker.twitterUrl)
        .withValue(ScheduleContract.Speakers.SPEAKER_IMPORT_HASHCODE,
            speaker.getImportHashcode())
        .build());
  }

  private void buildDeleteOperation(String speakerId, ArrayList<ContentProviderOperation> list) {
    Uri speakerUri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Speakers.buildSpeakerUri(speakerId));
    list.add(ContentProviderOperation.newDelete(speakerUri).build());
  }

  private HashMap<String, String> loadSpeakerHashcodes() {
    Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Speakers.CONTENT_URI);
    Cursor cursor = null;
    try {
      cursor = mContext.getContentResolver().query(uri, SpeakerHashcodeQuery.PROJECTION,
          null, null, null);
      if (cursor == null) {
        LOGE(TAG, "Error querying speaker hashcodes (got null cursor)");
        return null;
      }
      if (cursor.getCount() < 1) {
        LOGE(TAG, "Error querying speaker hashcodes (no records returned)");
        return null;
      }
      HashMap<String, String> result = new HashMap<>();
      if (cursor.moveToFirst()) {
        do {
          String speakerId = cursor.getString(SpeakerHashcodeQuery.SPEAKER_ID);
          String hashcode = cursor.getString(SpeakerHashcodeQuery.SPEAKER_IMPORT_HASHCODE);
          result.put(speakerId, hashcode == null ? "" : hashcode);
        } while (cursor.moveToNext());
      }
      return result;
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
  }

  public HashMap<String, Speaker> getSpeakerMap() {
    return mSpeakers;
  }

  private interface SpeakerHashcodeQuery {
    String[] PROJECTION = {
        BaseColumns._ID,
        ScheduleContract.Speakers.SPEAKER_ID,
        ScheduleContract.Speakers.SPEAKER_IMPORT_HASHCODE
    };
    final int _ID = 0;
    final int SPEAKER_ID = 1;
    final int SPEAKER_IMPORT_HASHCODE = 2;
  }
}
