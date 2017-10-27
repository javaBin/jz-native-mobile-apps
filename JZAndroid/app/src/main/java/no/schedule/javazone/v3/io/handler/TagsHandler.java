package no.schedule.javazone.v3.io.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import no.schedule.javazone.v3.io.JSONHandler;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.io.model.Tag;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.provider.ScheduleContractHelper;
import no.schedule.javazone.v3.provider.ScheduleDatabase;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class TagsHandler extends JSONHandler {
  private static final String TAG = makeLogTag(TagsHandler.class);

  private HashMap<String, Tag> mTags = new HashMap<>();

  public TagsHandler(Context context) {
    super(context);
  }

  @Override
  public void process(@NonNull List<Session> sessions) {
    for(Session session: sessions) {
      if (session.tags != null) {
        for (String tagName : session.tags) {
          if(tagName != null && !StringUtils.isEmpty(tagName)) {
            Tag tag = new Tag();
            tag.name = tagName;
            tag.id = tag.getImportedHashCode();
            mTags.put(tagName, new Tag());
          }
        }
      }
    }
  }

  @Override
  public void process(@NonNull Gson gson, @NonNull JsonElement element) {
    for (Tag tag : gson.fromJson(element, Tag[].class)) {
      mTags.put(tag.tag, tag);
    }
  }

  @Override
  public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
    Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Tags.CONTENT_URI);

    // since the number of tags is very small, for simplicity we delete them all and reinsert
    list.add(ContentProviderOperation.newDelete(uri).build());
    for (Tag tag : mTags.values()) {
      ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(uri);
      builder.withValue(ScheduleContract.Tags.TAG_ID, tag.id);
      builder.withValue(ScheduleContract.Tags.TAG_CATEGORY, tag.category);
      builder.withValue(ScheduleContract.Tags.TAG_NAME, tag.name);
      builder.withValue(ScheduleContract.Tags.TAG_ORDER_IN_CATEGORY, tag.order_in_category);
      builder.withValue(ScheduleContract.Tags.TAG_ABSTRACT, tag._abstract);
      builder.withValue(ScheduleContract.Tags.TAG_COLOR, tag.color == null ? 0
          : Color.parseColor(tag.color));
      builder.withValue(ScheduleContract.Tags.TAG_PHOTO_URL, tag.photoUrl);
      list.add(builder.build());
    }
  }

  public HashMap<String, Tag> getTagMap() {
    return mTags;
  }
}
