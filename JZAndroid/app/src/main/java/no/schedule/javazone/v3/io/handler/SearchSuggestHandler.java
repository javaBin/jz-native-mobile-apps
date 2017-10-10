package no.schedule.javazone.v3.io.handler;

import android.app.SearchManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import no.schedule.javazone.v3.io.JSONHandler;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.provider.ScheduleContractHelper;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class SearchSuggestHandler extends JSONHandler {
  private static final String TAG = makeLogTag(SpeakersHandler.class);
  HashSet<String> mSuggestions = new HashSet<>();

  public SearchSuggestHandler(Context context) {
    super(context);
  }

  @Override
  public void process(@NonNull Gson gson, @NonNull JsonElement element) {
    for (String word : gson.fromJson(element, String[].class)) {
      mSuggestions.add(word);
    }
  }

  @Override
  public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
    Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.SearchSuggest.CONTENT_URI);

    list.add(ContentProviderOperation.newDelete(uri).build());
    for (String word : mSuggestions) {
      list.add(ContentProviderOperation.newInsert(uri)
          .withValue(SearchManager.SUGGEST_COLUMN_TEXT_1, word)
          .build());
    }
  }

  @Override
  public void process(@NonNull List<Session> sessions) {

  }
}
