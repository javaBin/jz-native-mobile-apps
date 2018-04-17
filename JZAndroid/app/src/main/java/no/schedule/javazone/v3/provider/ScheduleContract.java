package no.schedule.javazone.v3.provider;

import android.app.SearchManager;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.util.List;

import no.schedule.javazone.v3.Config;
import no.schedule.javazone.v3.util.ParserUtils;

public class ScheduleContract {
  public static final String CONTENT_TYPE_APP_BASE = "javazone2017.";

  public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
      + CONTENT_TYPE_APP_BASE;

  public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
      + CONTENT_TYPE_APP_BASE;

  public interface SyncColumns {

    /** Last time this entry was updated or synchronized. */
    String UPDATED = "updated";
  }

  interface BlocksColumns {

    /** Unique string identifying this block of time. */
    String BLOCK_ID = "block_id";
    /** Title describing this block of time. */
    String BLOCK_TITLE = "block_title";
    /** Time when this block starts. */
    String BLOCK_START = "block_start";
    /** Time when this block ends. */
    String BLOCK_END = "block_end";
    /** Type describing this block. */
    String BLOCK_TYPE = "block_type";
    /** Extra subtitle for the block. */
    String BLOCK_SUBTITLE = "block_subtitle";
  }

  interface TagsColumns {

    /** Unique string identifying this tag. For example, "TOPIC_ANDROID", "TYPE_CODELAB" */
    String TAG_ID = "tag_id";
    /**
     * Tag category. For example, the tags that identify what topic a session pertains
     * to might belong to the "TOPIC" category; the tags that identify what type a session
     * is (codelab, office hours, etc) might belong to the "TYPE" category.
     */
    String TAG_CATEGORY = "tag_category";
    /** Tag name. For example, "Android". */
    String TAG_NAME = "tag_name";
    /** Tag's order in its category (for sorting). */
    String TAG_ORDER_IN_CATEGORY = "tag_order_in_category";
    /** Tag's color, in integer format. */
    String TAG_COLOR = "tag_color";
    /** Tag abstract. Short summary describing tag. */
    String TAG_ABSTRACT = "tag_abstract";
    /** The tag's photo Url. */
    String TAG_PHOTO_URL = "tag_photo_url";
  }

  interface RoomsColumns {

    /** Unique string identifying this room. */
    String ROOM_ID = "room_id";
    /** Name describing this room. */
    String ROOM_NAME = "room_name";
    /** Building floor this room exists on. */
    String ROOM_FLOOR = "room_floor";
  }

  interface MyScheduleColumns {

    String SESSION_ID = SessionsColumns.SESSION_ID;
    /** Account name for which the session is starred (in my schedule) */
    String MY_SCHEDULE_ACCOUNT_NAME = "account_name";
    /**
     * Indicate if last operation was "add" (true) or "remove" (false). Since uniqueness is
     * given by seesion_id+account_name, this field can be used as a way to find removals and
     * sync them with the cloud
     */
    String MY_SCHEDULE_IN_SCHEDULE = "in_schedule";
    /** Flag to indicate if the corresponding in_my_schedule item needs to be synced */
    String MY_SCHEDULE_DIRTY_FLAG = "dirty";
    String MY_SCHEDULE_TIMESTAMP = "timestamp";
  }

  interface SessionsColumns {
    // TODO must include new stuff here

    /** Unique string identifying this session. */
    String SESSION_ID = "session_id";
    /** Difficulty level of the session. */
    String SESSION_LEVEL = "session_level";
    /** Start time of this track. */
    String SESSION_START = "session_start";
    /** End time of this track. */
    String SESSION_END = "session_end";
    /** Title describing this track. */
    String SESSION_TITLE = "session_title";
    /** Body of text explaining this session in detail. */
    String SESSION_ABSTRACT = "session_abstract";
    String SESSION_INTENDED_AUDIENCE = "session_intended_audience";
    /** Requirements that attendees should meet. */
    String SESSION_REQUIREMENTS = "session_requirements";
    /** Kewords/tags for this session. */
    String SESSION_KEYWORDS = "session_keywords";
    /** Full URL to YouTube. */
    String SESSION_VIMEO_URL = "session_vimeo_url";
    /** User-specific flag indicating starred status. */
    String SESSION_IN_MY_SCHEDULE = "session_in_my_schedule";
    /** Key for session Calendar event. (Used in ICS or above) */
    String SESSION_CAL_EVENT_ID = "session_cal_event_id";
    /** The set of tags the session has. This is a comma-separated list of tags. */
    String SESSION_TAGS = "session_tags";
    /** The names of the speakers on this session, formatted for display. */
    String SESSION_SPEAKER_NAMES = "session_speaker_names";
    /** The hashcode of the data used to create this record. */
    String SESSION_IMPORT_HASHCODE = "session_import_hashcode";
  }

  interface SpeakersColumns {

    /** Unique string identifying this speaker. */
    String SPEAKER_ID = "speaker_id";
    /** Name of this speaker. */
    String SPEAKER_NAME = "speaker_name";
    /** Profile photo of this speaker. */
    String PICTURE_URL = "picture_url";
    /** Company this speaker works for. */
    String SPEAKER_COMPANY = "speaker_company";
    /** Body of text describing this speaker in detail. */
    String SPEAKER_ABSTRACT = "speaker_abstract";
    /** Deprecated. Full URL to the speaker's profile. */
    String SPEAKER_URL = "speaker_url";
    /** Full URL to the the speaker's Twitter profile. */
    String SPEAKER_TWITTER_URL = "twitter_url";
    /** The hashcode of the data used to create this record. */
    String SPEAKER_IMPORT_HASHCODE = "speaker_import_hashcode";
  }

  interface CardsColumns {
    /** Unique id for each card */
    String CARD_ID = "card_id";
    String TITLE = "title";
    /** URL for the action displayed on the card */
    String ACTION_URL = "action_url";
    /** Time when the card can start to be displayed */
    String DISPLAY_START_DATE = "start_date";
    /** Time when the card should no longer be displayed */
    String DISPLAY_END_DATE = "end_date";
    /** Extended message for the card */
    String MESSAGE = "message";
    String BACKGROUND_COLOR = "bg_color";
    String TEXT_COLOR = "text_color";
    String ACTION_COLOR = "action_color";
    String ACTION_TEXT = "action_text";
    String ACTION_TYPE = "action_type";
    String ACTION_EXTRA = "action_extra";
  }


  interface MapTileColumns {
    String TILE_FLOOR = "map_tile_floor";
    String TILE_FILE = "map_tile_file";
    String TILE_URL = "map_tile_url";
  }

  public static final String CONTENT_AUTHORITY = "no.schedule.javazone.v3";

  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

  private static final String PATH_BLOCKS = "blocks";
  private static final String PATH_AFTER = "after";
  private static final String PATH_CARDS = "cards";
  private static final String PATH_TAGS = "tags";
  private static final String PATH_ROOM = "room";
  private static final String PATH_UNSCHEDULED = "unscheduled";
  private static final String PATH_ROOMS = "rooms";
  private static final String PATH_SESSIONS = "sessions";
  private static final String PATH_MY_SCHEDULE = "my_schedule";
  private static final String PATH_SESSIONS_COUNTER = "counter";
  private static final String PATH_SPEAKERS = "speakers";
  private static final String PATH_MAP_FLOOR = "floor";
  private static final String PATH_MAP_TILES = "maptiles";

  private static final String PATH_SEARCH = "search";
  private static final String PATH_SEARCH_SUGGEST = "search_suggest_query";
  private static final String PATH_SEARCH_INDEX = "search_index";

  public static final String[] TOP_LEVEL_PATHS = {
      PATH_BLOCKS,
      PATH_TAGS,
      PATH_ROOMS,
      PATH_CARDS,
      PATH_SESSIONS,
      PATH_MY_SCHEDULE,
      PATH_SPEAKERS,
      PATH_MAP_FLOOR,
      PATH_MAP_TILES,
  };

  public static final String[] USER_DATA_RELATED_PATHS = {
      PATH_SESSIONS,
      PATH_MY_SCHEDULE
  };

  public static String makeContentType(String id) {
    if (id != null) {
      return CONTENT_TYPE_BASE + id;
    } else {
      return null;
    }
  }

  public static String makeContentItemType(String id) {
    if (id != null) {
      return CONTENT_ITEM_TYPE_BASE + id;
    } else {
      return null;
    }
  }

  /**
   * Blocks are generic timeslots.
   */
  public static class Blocks implements BlocksColumns, BaseColumns {

    public static final String BLOCK_TYPE_FREE = "free";

    public static final String BLOCK_TYPE_BREAK = "break";

    public static final String BLOCK_TYPE_KEYNOTE = "keynote";

    public static final String BLOCK_KIND_MEAL = "meal";

    public static final String BLOCK_KIND_CONCERT = "concert";

    public static final String BLOCK_KIND_AFTERHOURS = "afterHours";

    // TODO verify string value when backend starts reporting it
    public static final String BLOCK_KIND_BADGEPICKUP = "badgePickup";

    public static final boolean isValidBlockType(String type) {
      return BLOCK_TYPE_FREE.equals(type) || BLOCK_TYPE_BREAK.equals(type)
          || BLOCK_TYPE_KEYNOTE.equals(type);
    }

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_BLOCKS).build();

    public static final String CONTENT_TYPE_ID = "block";

    /** Build {@link Uri} for requested {@link #BLOCK_ID}. */
    public static Uri buildBlockUri(String blockId) {
      return CONTENT_URI.buildUpon().appendPath(blockId).build();
    }

    /** Read {@link #BLOCK_ID} from {@link Blocks} {@link Uri}. */
    public static String getBlockId(Uri uri) {
      return uri.getPathSegments().get(1);
    }

    /**
     * Generate a {@link #BLOCK_ID} that will always match the requested
     * {@link Blocks} details.
     *
     * @param startTime the block start time, in milliseconds since Epoch UTC
     * @param endTime   the block end time, in milliseconds since Epoch UTF
     */
    public static String generateBlockId(long startTime, long endTime) {
      startTime /= DateUtils.SECOND_IN_MILLIS;
      endTime /= DateUtils.SECOND_IN_MILLIS;
      return ParserUtils.sanitizeId(startTime + "-" + endTime);
    }
  }


  /**
   * Tags represent Session classifications. A session can have many tags. Tags can indicate,
   * for example, what product a session pertains to (Android, Chrome, ...), what type
   * of session it is (session, codelab, office hours, ...) and what overall event theme
   * it falls under (Design, Develop, Distribute), amongst others.
   */
  public static class Tags implements TagsColumns, BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_TAGS).build();

    public static final String CONTENT_TYPE_ID = "tag";

    /**
     * Build {@link Uri} that references all tags.
     */
    public static Uri buildTagsUri() {
      return CONTENT_URI;
    }

    /** Build a {@link Uri} that references a given tag. */
    public static Uri buildTagUri(String tagId) {
      return CONTENT_URI.buildUpon().appendPath(tagId).build();
    }

    /** Read {@link #TAG_ID} from {@link Tags} {@link Uri}. */
    public static String getTagId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  /**
   * MySchedule represent the sessions that the user has starred/added to the "my schedule".
   * Each row of MySchedule represents one session in one account's my schedule.
   */
  public static class MySchedule implements MyScheduleColumns, BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_MY_SCHEDULE).build();

    public static final String CONTENT_TYPE_ID = "myschedule";

    public static Uri buildMyScheduleUri(String accountName) {
      return ScheduleContractHelper.addOverrideAccountName(CONTENT_URI, accountName);
    }

  }

  /**
   * Cards are presented on the Explore I/O screen.
   */
  public static class Cards implements CardsColumns, BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARDS).build();

    public static final String CONTENT_TYPE_ID = "cards";

    /**
     * Build {@link Uri} that references any {@link Cards}.
     */
    public static Uri buildCardsUri() {
      return CONTENT_URI.buildUpon().appendPath(PATH_CARDS).build();
    }

    /** Build {@link Uri} for requested {@link #CARD_ID}. */
    public static Uri buildCardUri(String cardId) {
      return CONTENT_URI.buildUpon().appendPath(PATH_CARDS).appendPath(cardId).build();
    }
  }

  /**
   * Rooms are physical locations at the conference venue.
   */
  public static class Rooms implements RoomsColumns, BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROOMS).build();

    public static final String CONTENT_TYPE_ID = "room";

    /** Build {@link Uri} for requested {@link #ROOM_ID}. */
    public static Uri buildRoomUri(String roomId) {
      return CONTENT_URI.buildUpon().appendPath(roomId).build();
    }

    /**
     * Build {@link Uri} that references any {@link Sessions} associated
     * with the requested {@link #ROOM_ID}.
     */
    public static Uri buildSessionsDirUri(String roomId) {
      return CONTENT_URI.buildUpon().appendPath(roomId).appendPath(PATH_SESSIONS).build();
    }

    /** Read {@link #ROOM_ID} from {@link Rooms} {@link Uri}. */
    public static String getRoomId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  /**
   * Each session has zero or more {@link Tags}, a {@link Rooms},
   * zero or more {@link Speakers}.
   */
  public static class Sessions implements SessionsColumns, RoomsColumns,
      SyncColumns, BaseColumns {

    public static final String QUERY_PARAMETER_TAG_FILTER = "filter";
    public static final String QUERY_PARAMETER_CATEGORIES = "categories";

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_SESSIONS).build();

    public static final Uri CONTENT_MY_SCHEDULE_URI =
        CONTENT_URI.buildUpon().appendPath(PATH_MY_SCHEDULE).build();

    public static final String CONTENT_TYPE_ID = "session";

    public static final String ROOM_ID = "room_id";

    public static final String SEARCH_SNIPPET = "search_snippet";

    // ORDER BY clauses
    public static final String SORT_BY_TIME = SESSION_START + " ASC," + SESSION_TITLE
        + " COLLATE NOCASE ASC";

    // Keynotes are always bookmarked and in "my schedule"
    public static final String IN_SCHEDULE_SELECTION = SESSION_IN_MY_SCHEDULE + " = 1 OR " +
        Sessions.SESSION_TAGS + " LIKE '%" + Config.Tags.SPECIAL_KEYNOTE + "%'";

    // Keynotes are always bookmarked and in "my schedule"
    public static final String NOT_IN_SCHEDULE_SELECTION =
        "NOT (" + IN_SCHEDULE_SELECTION + ")";

    // Used to fetch sessions starting within a specific time interval
    public static final String STARTING_AT_TIME_INTERVAL_SELECTION =
        SESSION_START + " >= ? and " + SESSION_START + " <= ?";

    // Used to fetch sessions for a particular time
    public static final String AT_TIME_SELECTION =
        SESSION_START + " <= ? and " + SESSION_END + " >= ?";

    // Builds selectionArgs for {@link STARTING_AT_TIME_INTERVAL_SELECTION}
    public static String[] buildAtTimeIntervalArgs(long intervalStart, long intervalEnd) {
      return new String[]{String.valueOf(intervalStart), String.valueOf(intervalEnd)};
    }

    // Builds selectionArgs for {@link AT_TIME_SELECTION}
    public static String[] buildAtTimeSelectionArgs(long time) {
      final String timeString = String.valueOf(time);
      return new String[]{timeString, timeString};
    }

    // Used to fetch upcoming sessions
    public static final String UPCOMING_LIVE_SELECTION = SESSION_START + " > ?";

    // Builds selectionArgs for {@link UPCOMING_LIVE_SELECTION}
    public static String[] buildUpcomingSelectionArgs(long minTime) {
      return new String[]{String.valueOf(minTime)};
    }

    /** Build {@link Uri} for requested {@link #SESSION_ID}. */
    public static Uri buildSessionUri(String sessionId) {
      return CONTENT_URI.buildUpon().appendPath(sessionId).build();
    }

    /**
     * Build {@link Uri} that references any {@link Speakers} associated
     * with the requested {@link #SESSION_ID}.
     */
    public static Uri buildSpeakersDirUri(String sessionId) {
      return CONTENT_URI.buildUpon().appendPath(sessionId).appendPath(PATH_SPEAKERS).build();
    }

    /**
     * Build {@link Uri} that references any {@link Tags} associated with
     * the requested {@link #SESSION_ID}.
     */
    public static Uri buildTagsDirUri(String sessionId) {
      return CONTENT_URI.buildUpon().appendPath(sessionId).appendPath(PATH_TAGS).build();
    }

    /**
     * Build {@link Uri} that references sessions that match the query. The query can be
     * multiple words separated with spaces.
     *
     * @param query The query. Can be multiple words separated by spaces.
     * @return {@link Uri} to the sessions
     */
    public static Uri buildSearchUri(String query) {
      if (null == query) {
        query = "";
      }
      // convert "lorem ipsum dolor sit" to "lorem* ipsum* dolor* sit*"
      query = query.replaceAll(" +", " *") + "*";
      return CONTENT_URI.buildUpon()
          .appendPath(PATH_SEARCH).appendPath(query).build();
    }

    public static boolean isSearchUri(Uri uri) {
      List<String> pathSegments = uri.getPathSegments();
      return pathSegments.size() >= 2 && PATH_SEARCH.equals(pathSegments.get(1));
    }

    /**
     * Build {@link Uri} that references sessions in a room that have begun after the requested
     * time *
     */
    public static Uri buildSessionsInRoomAfterUri(String room, long time) {
      return CONTENT_URI.buildUpon().appendPath(PATH_ROOM).appendPath(room)
          .appendPath(PATH_AFTER)
          .appendPath(String.valueOf(time)).build();
    }

    /**
     * Build {@link Uri} that references sessions that have begun after the requested time.
     */
    public static Uri buildSessionsAfterUri(long time) {
      return CONTENT_URI.buildUpon().appendPath(PATH_AFTER)
          .appendPath(String.valueOf(time)).build();
    }

    /**
     * Build {@link Uri} that references sessions not in user's schedule that happen in the
     * specified interval *
     */
    public static Uri buildUnscheduledSessionsInInterval(long start, long end) {
      String interval = start + "-" + end;
      return CONTENT_URI.buildUpon().appendPath(PATH_UNSCHEDULED).appendPath(interval)
          .build();
    }

    public static boolean isUnscheduledSessionsInInterval(Uri uri) {
      return uri != null && uri.toString().startsWith(
          CONTENT_URI.buildUpon().appendPath(PATH_UNSCHEDULED).toString());
    }

    public static long[] getInterval(Uri uri) {
      if (uri == null) {
        return null;
      }
      List<String> segments = uri.getPathSegments();
      if (segments.size() == 3 && segments.get(2).indexOf('-') > 0) {
        String[] interval = segments.get(2).split("-");
        return new long[]{Long.parseLong(interval[0]), Long.parseLong(interval[1])};
      }
      return null;
    }

    public static String getRoom(Uri uri) {
      return uri.getPathSegments().get(2);
    }

    public static String getAfterForRoom(Uri uri) {
      return uri.getPathSegments().get(4);
    }

    public static String getAfter(Uri uri) {
      return uri.getPathSegments().get(2);
    }


    /** Read {@link #SESSION_ID} from {@link Sessions} {@link Uri}. */
    public static String getSessionId(Uri uri) {
      return uri.getPathSegments().get(1);
    }

    public static String getSearchQuery(Uri uri) {
      List<String> segments = uri.getPathSegments();
      if (2 < segments.size()) {
        return segments.get(2);
      }
      return null;
    }

    public static boolean hasFilterParam(Uri uri) {
      return uri != null && uri.getQueryParameter(QUERY_PARAMETER_TAG_FILTER) != null;
    }

    /**
     * Build {@link Uri} that references all sessions that have ALL of the indicated tags.
     * @param contentUri The base Uri that is used for adding the required tags.
     * @param requiredTags The tags that are used for creating the query parameter.
     * @return uri The uri updated to include the indicated tags.
     */
    @Deprecated
    public static Uri buildTagFilterUri(Uri contentUri, String[] requiredTags) {
      return buildCategoryTagFilterUri(contentUri, requiredTags,
          requiredTags == null ? 0 : requiredTags.length);
    }

    /** Build {@link Uri} that references all sessions that have ALL of the indicated tags. */
    @Deprecated
    public static Uri buildTagFilterUri(String[] requiredTags) {
      return buildTagFilterUri(CONTENT_URI, requiredTags);
    }

    /**
     * Build {@link Uri} that references all sessions that have the following tags and
     * satisfy the requirement of containing ALL the categories
     * @param contentUri The base Uri that is used for adding the query parameters.
     * @param tags The various tags that can include topics, themes as well as types.
     * @param categories The number of categories that are required. At most this can be 3,
     *                   since a session can belong only to one type + topic + theme.
     * @return Uri representing the query parameters for the filter as well as the categories.
     */
    public static Uri buildCategoryTagFilterUri(Uri contentUri, String[] tags, int categories) {
      if (tags == null || tags.length < 1) {
        return contentUri;
      }
      StringBuilder sb = new StringBuilder();
      for (String tag : tags) {
        if (TextUtils.isEmpty(tag)) {
          continue;
        }
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(tag.trim());
      }
      if (sb.length() == 0) {
        return contentUri;
      } else {
        return contentUri.buildUpon()
            .appendQueryParameter(QUERY_PARAMETER_TAG_FILTER, sb.toString())
            .appendQueryParameter(QUERY_PARAMETER_CATEGORIES,
                String.valueOf(categories))
            .build();
      }
    }

    /** Build {@link Uri} that counts sessions by start/end intervals. */
    public static Uri buildCounterByIntervalUri() {
      return CONTENT_URI.buildUpon().appendPath(PATH_SESSIONS_COUNTER).build();
    }
  }

  /**
   * Speakers are individual people that lead {@link Sessions}.
   */
  public static class Speakers implements SpeakersColumns, SyncColumns, BaseColumns {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_SPEAKERS).build();

    public static final String CONTENT_TYPE_ID = "speaker";

    /** Default "ORDER BY" clause. */
    public static final String DEFAULT_SORT = SpeakersColumns.SPEAKER_NAME
        + " COLLATE NOCASE ASC";

    /** Build {@link Uri} for requested {@link #SPEAKER_ID}. */
    public static Uri buildSpeakerUri(String speakerId) {
      return CONTENT_URI.buildUpon().appendPath(speakerId).build();
    }

    /** Read {@link #SPEAKER_ID} from {@link Speakers} {@link Uri}. */
    public static String getSpeakerId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }


  /**
   * TileProvider entries are used to create an overlay provider for the map.
   */
  public static class MapTiles implements MapTileColumns, BaseColumns {

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
        .appendPath(PATH_MAP_TILES).build();

    public static final String CONTENT_TYPE_ID = "maptiles";

    /** Build {@link Uri} for all overlay zoom entries. */
    public static Uri buildUri() {
      return CONTENT_URI;
    }

    /** Build {@link Uri} for requested floor. */
    public static Uri buildFloorUri(String floor) {
      return CONTENT_URI.buildUpon()
          .appendPath(String.valueOf(floor)).build();
    }
  }


  public static class SearchSuggest {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_SUGGEST).build();

    public static final String DEFAULT_SORT = SearchManager.SUGGEST_COLUMN_TEXT_1
        + " COLLATE NOCASE ASC";
  }

  public static class SearchIndex {

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_INDEX).build();
  }

  public static class SearchTopicsSessions {
    public static final String PATH_SEARCH_TOPICS_SESSIONS = "search_topics_sessions";

    public static final String CONTENT_TYPE_ID = "search_topics_sessions";

    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_TOPICS_SESSIONS).build();

    public static final String TOPIC_TAG_SELECTION = Tags.TAG_CATEGORY + "= ? and " +
        Tags.TAG_NAME + " like ?";

    public static final String TOPIC_TAG_SORT = Tags.TAG_NAME + " ASC";

    public static final String[] TOPIC_TAG_PROJECTION = {
        BaseColumns._ID,
        Tags.TAG_ID,
        Tags.TAG_NAME,
    };

    public static final String[] SEARCH_SESSIONS_PROJECTION = {
        BaseColumns._ID,
        ScheduleContract.Sessions.SESSION_ID,
        ScheduleContract.Sessions.SEARCH_SNIPPET
    };

    public static final String[] DEFAULT_PROJECTION = new String[] {
        BaseColumns._ID,
        SearchTopicSessionsColumns.TAG_OR_SESSION_ID,
        SearchTopicSessionsColumns.SEARCH_SNIPPET,
        SearchTopicSessionsColumns.IS_TOPIC_TAG,
    };
  }

  /**
   * Columns for an in memory table created on query using
   * the Tags table and the SearchSessions table.
   */
  public interface SearchTopicSessionsColumns extends BaseColumns {
    /* This column contains either a tag_id or a session_id */
    String TAG_OR_SESSION_ID = "tag_or_session_id";
    /* This column contains the search snippet to be shown to the user.*/
    String SEARCH_SNIPPET = "search_snippet";
    /* Indicates whether this row is a topic tag or a session_id. */
    String IS_TOPIC_TAG = "is_topic_tag";
  }

  private ScheduleContract() {
  }
}
