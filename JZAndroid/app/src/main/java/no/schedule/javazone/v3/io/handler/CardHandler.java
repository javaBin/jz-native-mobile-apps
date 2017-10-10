package no.schedule.javazone.v3.io.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import no.schedule.javazone.v3.io.JSONHandler;
import no.schedule.javazone.v3.io.model.Card;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.provider.ScheduleContractHelper;

import static no.schedule.javazone.v3.util.LogUtils.LOGI;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class CardHandler extends JSONHandler {
  private static final String TAG = makeLogTag(CardHandler.class);

  // Map keyed on Card IDs.
  private HashMap<String, Card> mCards = new HashMap<>();

  public CardHandler(Context context) {
    super(context);
  }

  @Override
  public void process(@NonNull Gson gson, @NonNull JsonElement element) {
    for (Card card : gson.fromJson(element, Card[].class)) {
      //mCards.put(card, card);
    }
  }

  @Override
  public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
    LOGI(TAG, "Creating content provider operations for cards: " + mCards.size());
    Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Cards.CONTENT_URI);

    /*
    // The list of cards is not large, so for simplicity we delete all of them and repopulate
    list.add(ContentProviderOperation.newDelete(uri).build());
    for (Card card : mCards.values()) {
      ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(uri);
      builder.withValue(ScheduleContract.Cards.ACTION_COLOR, card.mActionColor);
      builder.withValue(ScheduleContract.Cards.ACTION_TEXT, card.mActionText);
      builder.withValue(ScheduleContract.Cards.ACTION_URL, card.mActionUrl);
      builder.withValue(ScheduleContract.Cards.ACTION_TYPE, card.mActionType);
      builder.withValue(ScheduleContract.Cards.ACTION_EXTRA, card.mActionExtra);
      builder.withValue(ScheduleContract.Cards.BACKGROUND_COLOR, card.mBackgroundColor);
      builder.withValue(ScheduleContract.Cards.CARD_ID, card.mId);
      try {
        long startTime = Card.getEpochMillisFromTimeString(card.mValidFrom);
        LOGI(TAG, "Processing card with epoch start time: " + startTime);
        builder.withValue(ScheduleContract.Cards.DISPLAY_START_DATE, startTime);
      } catch (IllegalArgumentException exception) {
        LOGE(TAG, "Card time disabled, invalid display start date defined for card: " +
            card.mTitle + " " + card.mValidFrom);
        builder.withValue(ScheduleContract.Cards.DISPLAY_START_DATE, Long.MAX_VALUE);
      }
      try {
        long endTime = Card.getEpochMillisFromTimeString(card.mValidUntil);
        LOGI(TAG, "Processing card with epoch end time: " + endTime);
        builder.withValue(ScheduleContract.Cards.DISPLAY_END_DATE, endTime);
      } catch (IllegalArgumentException exception) {
        LOGE(TAG, "Card time disabled, invalid display end date defined for card: " +
            card.mTitle + " " + card.mValidUntil);
        builder.withValue(ScheduleContract.Cards.DISPLAY_END_DATE, 0L);
      }
      builder.withValue(ScheduleContract.Cards.MESSAGE, card.mShortMessage);
      builder.withValue(ScheduleContract.Cards.TEXT_COLOR, card.mTextColor);
      builder.withValue(ScheduleContract.Cards.TITLE, card.mTitle);
      list.add(builder.build());
    } */
  }

  @Override
  public void process(@NonNull List<Session> sessions) {

  }
}
