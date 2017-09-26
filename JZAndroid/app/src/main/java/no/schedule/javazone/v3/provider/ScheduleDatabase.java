package no.schedule.javazone.v3.provider;

import android.accounts.Account;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import no.schedule.javazone.v3.BuildConfig;
import no.schedule.javazone.v3.provider.ScheduleContract.*;
import no.schedule.javazone.v3.sync.ConferenceDataHandler;
import no.schedule.javazone.v3.sync.SessionApiWebService;
import no.schedule.javazone.v3.util.AccountUtils;

import static no.schedule.javazone.v3.util.LogUtils.LOGD;
import static no.schedule.javazone.v3.util.LogUtils.LOGI;
import static no.schedule.javazone.v3.util.LogUtils.LOGW;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

/**
 * Helper for managing {@link SQLiteDatabase} that stores data for {@link ScheduleProvider}.
 */
public class ScheduleDatabase extends SQLiteOpenHelper {
  private static final String TAG = makeLogTag(ScheduleDatabase.class);

  private static final String DATABASE_NAME = "jz2017.db";
  private static final int VER_2017_RELEASE_D = 216;
  private static final int CUR_DATABASE_VERSION = VER_2017_RELEASE_D;

  private final Context mContext;

  public interface Tables {
    String BLOCKS = "blocks";
    String CARDS = "cards";
    String TAGS = "tags";
    String ROOMS = "rooms";
    String SESSIONS = "sessions";
    String MY_SCHEDULE = "myschedule";
    String SPEAKERS = "speakers";
    String SESSIONS_TAGS = "sessions_tags";
    String SESSIONS_SPEAKERS = "sessions_speakers";
    String MAPTILES = "mapoverlays";
    String HASHTAGS = "hashtags";

    String SESSIONS_SEARCH = "sessions_search";
    String SEARCH_SUGGEST = "search_suggest";

    String SESSIONS_JOIN_MYSCHEDULE = "sessions "
        + "LEFT OUTER JOIN myschedule ON sessions.session_id=myschedule.session_id "
        + "AND myschedule.account_name=? ";

    String SESSIONS_JOIN_ROOMS_TAGS = "sessions "
        + "LEFT OUTER JOIN myschedule ON sessions.session_id=myschedule.session_id "
        + "AND myschedule.account_name=? "
        + "LEFT OUTER JOIN rooms ON sessions.room_id=rooms.room_id "
        + "LEFT OUTER JOIN sessions_tags ON sessions.session_id=sessions_tags.session_id ";

    String SESSIONS_JOIN_ROOMS_TAGS_FEEDBACK_MYSCHEDULE = "sessions "
        + "LEFT OUTER JOIN myschedule ON sessions.session_id=myschedule.session_id "
        + "AND myschedule.account_name=? "
        + "LEFT OUTER JOIN rooms ON sessions.room_id=rooms.room_id "
        + "LEFT OUTER JOIN sessions_tags ON sessions.session_id=sessions_tags.session_id ";

    String SESSIONS_JOIN_ROOMS = "sessions "
        + "LEFT OUTER JOIN myschedule ON sessions.session_id=myschedule.session_id "
        + "AND myschedule.account_name=? "
        + "LEFT OUTER JOIN rooms ON sessions.room_id=rooms.room_id ";

    String SESSIONS_SPEAKERS_JOIN_SPEAKERS = "sessions_speakers "
        + "LEFT OUTER JOIN speakers ON sessions_speakers.speaker_id=speakers.speaker_id";

    String SESSIONS_TAGS_JOIN_TAGS = "sessions_tags "
        + "LEFT OUTER JOIN tags ON sessions_tags.tag_id=tags.tag_id";

    String SESSIONS_SPEAKERS_JOIN_SESSIONS_ROOMS = "sessions_speakers "
        + "LEFT OUTER JOIN sessions ON sessions_speakers.session_id=sessions.session_id "
        + "LEFT OUTER JOIN rooms ON sessions.room_id=rooms.room_id "
        + "LEFT OUTER JOIN myschedule ON sessions.session_id=myschedule.session_id "
        + "AND myschedule.account_name=? ";

    String SESSIONS_SEARCH_JOIN_SESSIONS_ROOMS = "sessions_search "
        + "LEFT OUTER JOIN sessions ON sessions_search.session_id=sessions.session_id "
        + "LEFT OUTER JOIN myschedule ON sessions.session_id=myschedule.session_id "
        + "AND myschedule.account_name=? "
        + "LEFT OUTER JOIN rooms ON sessions.room_id=rooms.room_id ";
  }

  private interface Triggers {
    // Deletes from dependent tables when corresponding sessions are deleted.
    String SESSIONS_TAGS_DELETE = "sessions_tags_delete";
    String SESSIONS_SPEAKERS_DELETE = "sessions_speakers_delete";
    String SESSIONS_MY_SCHEDULE_DELETE = "sessions_myschedule_delete";
  }

  public interface SessionsSpeakers {
    String SESSION_ID = "session_id";
    String SPEAKER_ID = "speaker_id";
  }

  public interface SessionsTags {
    String SESSION_ID = "session_id";
    String TAG_ID = "tag_id";
  }

  interface SessionsSearchColumns {
    String SESSION_ID = "session_id";
    String BODY = "body";
  }

  /**
   * Fully-qualified field names.
   */
  private interface Qualified {
    String SESSIONS_SEARCH = Tables.SESSIONS_SEARCH + "(" + SessionsSearchColumns.SESSION_ID
        + "," + SessionsSearchColumns.BODY + ")";

    String SESSIONS_TAGS_SESSION_ID = Tables.SESSIONS_TAGS + "."
        + SessionsTags.SESSION_ID;

    String SESSIONS_SPEAKERS_SESSION_ID = Tables.SESSIONS_SPEAKERS + "."
        + SessionsSpeakers.SESSION_ID;

    String SESSIONS_SPEAKERS_SPEAKER_ID = Tables.SESSIONS_SPEAKERS + "."
        + SessionsSpeakers.SPEAKER_ID;

    String SPEAKERS_SPEAKER_ID = Tables.SPEAKERS + "." + ScheduleContract.Speakers.SPEAKER_ID;
  }

  /**
   * {@code REFERENCES} clauses.
   */
  private interface References {
    String BLOCK_ID = "REFERENCES " + Tables.BLOCKS + "(" + ScheduleContract.Blocks.BLOCK_ID + ")";
    String TAG_ID = "REFERENCES " + Tables.TAGS + "(" + ScheduleContract.Tags.TAG_ID + ")";
    String ROOM_ID = "REFERENCES " + Tables.ROOMS + "(" + ScheduleContract.Rooms.ROOM_ID + ")";
    String SESSION_ID = "REFERENCES " + Tables.SESSIONS + "(" + ScheduleContract.Sessions.SESSION_ID + ")";
    String SPEAKER_ID = "REFERENCES " + Tables.SPEAKERS + "(" + ScheduleContract.Speakers.SPEAKER_ID + ")";
  }

  public ScheduleDatabase(Context context) {
    super(context, DATABASE_NAME, null, CUR_DATABASE_VERSION);
    mContext = context;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + Tables.BLOCKS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + BlocksColumns.BLOCK_ID + " TEXT NOT NULL,"
        + BlocksColumns.BLOCK_TITLE + " TEXT NOT NULL,"
        + BlocksColumns.BLOCK_START + " INTEGER NOT NULL,"
        + BlocksColumns.BLOCK_END + " INTEGER NOT NULL,"
        + BlocksColumns.BLOCK_TYPE + " TEXT,"
        + BlocksColumns.BLOCK_SUBTITLE + " TEXT,"
        + "UNIQUE (" + BlocksColumns.BLOCK_ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.TAGS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + TagsColumns.TAG_ID + " TEXT NOT NULL,"
        + TagsColumns.TAG_CATEGORY + " TEXT NOT NULL,"
        + TagsColumns.TAG_NAME + " TEXT NOT NULL,"
        + TagsColumns.TAG_ORDER_IN_CATEGORY + " INTEGER,"
        + TagsColumns.TAG_COLOR + " TEXT NOT NULL,"
        + TagsColumns.TAG_ABSTRACT + " TEXT NOT NULL,"
        + "UNIQUE (" + TagsColumns.TAG_ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.ROOMS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + RoomsColumns.ROOM_ID + " TEXT NOT NULL,"
        + RoomsColumns.ROOM_NAME + " TEXT,"
        + RoomsColumns.ROOM_FLOOR + " TEXT,"
        + "UNIQUE (" + RoomsColumns.ROOM_ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.CARDS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + Cards.ACTION_COLOR + " TEXT, "
        + Cards.ACTION_TEXT + " TEXT, "
        + Cards.ACTION_URL + " TEXT, "
        + Cards.BACKGROUND_COLOR + " TEXT, "
        + Cards.CARD_ID + " TEXT, "
        + Cards.DISPLAY_END_DATE + " INTEGER, "
        + Cards.DISPLAY_START_DATE + " INTEGER, "
        + Cards.MESSAGE + " TEXT, "
        + Cards.TEXT_COLOR + " TEXT, "
        + Cards.TITLE + " TEXT,  "
        + Cards.ACTION_TYPE + " TEXT,  "
        + Cards.ACTION_EXTRA + " TEXT, "
        + "UNIQUE (" + Cards.CARD_ID + ") ON CONFLICT REPLACE)");

    // TODO must include new stuff here


    db.execSQL("CREATE TABLE " + Tables.SESSIONS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + SyncColumns.UPDATED + " INTEGER NOT NULL,"
        + SessionsColumns.SESSION_ID + " TEXT NOT NULL,"
        + Sessions.ROOM_ID + " TEXT " + References.ROOM_ID + ","
        + SessionsColumns.SESSION_START + " INTEGER NOT NULL,"
        + SessionsColumns.SESSION_END + " INTEGER NOT NULL,"
        + SessionsColumns.SESSION_LEVEL + " TEXT,"
        + SessionsColumns.SESSION_TITLE + " TEXT,"
        + SessionsColumns.SESSION_ABSTRACT + " TEXT,"
        + SessionsColumns.SESSION_REQUIREMENTS + " TEXT,"
        + SessionsColumns.SESSION_KEYWORDS + " TEXT,"
        + SessionsColumns.SESSION_VIMEO_URL + " TEXT,"
        + SessionsColumns.SESSION_MODERATOR_URL + " TEXT,"
        + SessionsColumns.SESSION_NOTES_URL + " TEXT,"
        + SessionsColumns.SESSION_CAL_EVENT_ID + " INTEGER,"
        + SessionsColumns.SESSION_LIVESTREAM_ID + " TEXT,"
        + SessionsColumns.SESSION_TAGS + " TEXT,"
        + SessionsColumns.SESSION_SPEAKER_NAMES + " TEXT,"
        + SessionsColumns.SESSION_IMPORT_HASHCODE + " TEXT NOT NULL DEFAULT '',"
        + SessionsColumns.SESSION_MAIN_TAG + " TEXT,"
        + SessionsColumns.SESSION_COLOR + " INTEGER,"
        + ScheduleContract.SessionsColumns.SESSION_CAPTIONS_URL + " TEXT,"
        + SessionsColumns.SESSION_PHOTO_URL + " TEXT,"
        + "UNIQUE (" + SessionsColumns.SESSION_ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.SPEAKERS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + SyncColumns.UPDATED + " INTEGER NOT NULL,"
        + SpeakersColumns.SPEAKER_ID + " TEXT NOT NULL,"
        + SpeakersColumns.SPEAKER_NAME + " TEXT,"
        + SpeakersColumns.SPEAKER_IMAGE_URL + " TEXT,"
        + SpeakersColumns.SPEAKER_COMPANY + " TEXT,"
        + SpeakersColumns.SPEAKER_ABSTRACT + " TEXT,"
        + SpeakersColumns.SPEAKER_URL + " TEXT,"
        + SpeakersColumns.SPEAKER_IMPORT_HASHCODE + " TEXT NOT NULL DEFAULT '',"
        + SpeakersColumns.SPEAKER_TWITTER_URL + " Text,"
        + "UNIQUE (" + SpeakersColumns.SPEAKER_ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.MY_SCHEDULE + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + MySchedule.SESSION_ID + " TEXT NOT NULL " + References.SESSION_ID + ","
        + MySchedule.MY_SCHEDULE_ACCOUNT_NAME + " TEXT NOT NULL,"
        + MySchedule.MY_SCHEDULE_DIRTY_FLAG + " INTEGER NOT NULL DEFAULT 1,"
        + MySchedule.MY_SCHEDULE_IN_SCHEDULE + " INTEGER NOT NULL DEFAULT 1,"
        + "UNIQUE (" + MySchedule.SESSION_ID + ","
        + MySchedule.MY_SCHEDULE_ACCOUNT_NAME + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.SESSIONS_SPEAKERS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + SessionsSpeakers.SESSION_ID + " TEXT NOT NULL " + References.SESSION_ID + ","
        + SessionsSpeakers.SPEAKER_ID + " TEXT NOT NULL " + References.SPEAKER_ID + ","
        + "UNIQUE (" + SessionsSpeakers.SESSION_ID + ","
        + SessionsSpeakers.SPEAKER_ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.SESSIONS_TAGS + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + SessionsTags.SESSION_ID + " TEXT NOT NULL " + References.SESSION_ID + ","
        + SessionsTags.TAG_ID + " TEXT NOT NULL " + References.TAG_ID + ","
        + "UNIQUE (" + SessionsTags.SESSION_ID + ","
        + SessionsTags.TAG_ID + ") ON CONFLICT REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.MAPTILES + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + MapTileColumns.TILE_FLOOR + " INTEGER NOT NULL,"
        + MapTileColumns.TILE_FILE + " TEXT NOT NULL,"
        + MapTileColumns.TILE_URL + " TEXT NOT NULL,"
        + "UNIQUE (" + MapTileColumns.TILE_FLOOR + ") ON CONFLICT REPLACE)");

    // Full-text search index. Update using updateSessionSearchIndex method.
    // Use the porter tokenizer for simple stemming, so that "frustration" matches "frustrated."
    db.execSQL("CREATE VIRTUAL TABLE " + Tables.SESSIONS_SEARCH + " USING fts3("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + SessionsSearchColumns.BODY + " TEXT NOT NULL,"
        + SessionsSearchColumns.SESSION_ID
        + " TEXT NOT NULL " + References.SESSION_ID + ","
        + "UNIQUE (" + SessionsSearchColumns.SESSION_ID + ") ON CONFLICT REPLACE,"
        + "tokenize=porter)");

    // Search suggestions
    db.execSQL("CREATE TABLE " + Tables.SEARCH_SUGGEST + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + SearchManager.SUGGEST_COLUMN_TEXT_1 + " TEXT NOT NULL)");

    // Session deletion triggers
    db.execSQL("CREATE TRIGGER " + Triggers.SESSIONS_TAGS_DELETE + " AFTER DELETE ON "
        + Tables.SESSIONS + " BEGIN DELETE FROM " + Tables.SESSIONS_TAGS + " "
        + " WHERE " + Qualified.SESSIONS_TAGS_SESSION_ID + "=old." + Sessions.SESSION_ID
        + ";" + " END;");

    db.execSQL("CREATE TRIGGER " + Triggers.SESSIONS_SPEAKERS_DELETE + " AFTER DELETE ON "
        + Tables.SESSIONS + " BEGIN DELETE FROM " + Tables.SESSIONS_SPEAKERS + " "
        + " WHERE " + Qualified.SESSIONS_SPEAKERS_SESSION_ID + "=old." + Sessions.SESSION_ID
        + ";" + " END;");

    db.execSQL("CREATE TRIGGER " + Triggers.SESSIONS_MY_SCHEDULE_DELETE + " AFTER DELETE ON "
        + Tables.SESSIONS + " BEGIN DELETE FROM " + Tables.MY_SCHEDULE + " "
        + " WHERE " + Tables.MY_SCHEDULE + "." + MySchedule.SESSION_ID +
        "=old." + Sessions.SESSION_ID
        + ";" + " END;");


    // Adds a timestamp value to my schedule. Used when syncing and merging local and remote
    // data with the version having the more recent timestamp assuming precedence.
    db.execSQL("ALTER TABLE " + Tables.MY_SCHEDULE
        + " ADD COLUMN " + MyScheduleColumns.MY_SCHEDULE_TIMESTAMP + " DATETIME");

    // Note: Adding photoUrl to tags
    db.execSQL("ALTER TABLE " + Tables.TAGS
        + " ADD COLUMN " + TagsColumns.TAG_PHOTO_URL + " TEXT");

  }


  /**
   * Updates the session search index. This should be done sparingly, as the queries are rather
   * complex.
   */
  static void updateSessionSearchIndex(SQLiteDatabase db) {
    db.execSQL("DELETE FROM " + Tables.SESSIONS_SEARCH);

    db.execSQL("INSERT INTO " + Qualified.SESSIONS_SEARCH
        + " SELECT s." + Sessions.SESSION_ID + ",("

        // Full text body
        + Sessions.SESSION_TITLE + "||'; '||"
        + Sessions.SESSION_ABSTRACT + "||'; '||"
        + "IFNULL(GROUP_CONCAT(t." + Speakers.SPEAKER_NAME + ",' '),'')||'; '||"
        + "'')"

        + " FROM " + Tables.SESSIONS + " s "
        + " LEFT OUTER JOIN"

        // Subquery resulting in session_id, speaker_id, speaker_name
        + "(SELECT " + Sessions.SESSION_ID + "," + Qualified.SPEAKERS_SPEAKER_ID
        + "," + Speakers.SPEAKER_NAME
        + " FROM " + Tables.SESSIONS_SPEAKERS
        + " INNER JOIN " + Tables.SPEAKERS
        + " ON " + Qualified.SESSIONS_SPEAKERS_SPEAKER_ID + "="
        + Qualified.SPEAKERS_SPEAKER_ID
        + ") t"

        // Grand finale
        + " ON s." + Sessions.SESSION_ID + "=t." + Sessions.SESSION_ID
        + " GROUP BY s." + Sessions.SESSION_ID);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    LOGD(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);

    // Cancel any sync currently in progress
    Account account = AccountUtils.getActiveAccount(mContext);
    if (account != null) {
      LOGI(TAG, "Cancelling any pending syncs for account");
      ContentResolver.cancelSync(account, ScheduleContract.CONTENT_AUTHORITY);
    }

    // Current DB version. We update this variable as we perform upgrades to reflect
    // the current version we are in.
    int version = oldVersion;

    // Indicates whether the data we currently have should be invalidated as a
    // result of the db upgrade. Default is true (invalidate); if we detect that this
    // is a trivial DB upgrade, we set this to false.
    boolean dataInvalidated = true;


    LOGD(TAG, "Upgrading database from 2017 release C to 2017 release D.");
    version = VER_2017_RELEASE_D;
    LOGD(TAG, "After upgrade logic, at version " + version);

    // At this point, we ran out of upgrade logic, so if we are still at the wrong
    // version, we have no choice but to delete everything and create everything again.
    if (version != CUR_DATABASE_VERSION) {
      LOGW(TAG, "Upgrade unsuccessful -- destroying old data during upgrade");

      // Drop triggers and tables in reverse order of creation.

      db.execSQL("DROP TABLE IF EXISTS " + Tables.CARDS);

      db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.SESSIONS_MY_SCHEDULE_DELETE);
      db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.SESSIONS_SPEAKERS_DELETE);
      db.execSQL("DROP TRIGGER IF EXISTS " + Triggers.SESSIONS_TAGS_DELETE);

      db.execSQL("DROP TABLE IF EXISTS " + Tables.SEARCH_SUGGEST);
      db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS_SEARCH);
      db.execSQL("DROP TABLE IF EXISTS " + Tables.HASHTAGS);

      db.execSQL("DROP TABLE IF EXISTS " + Tables.MAPTILES);
      db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS_TAGS);
      db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS_SPEAKERS);
      db.execSQL("DROP TABLE IF EXISTS " + Tables.MY_SCHEDULE);
      db.execSQL("DROP TABLE IF EXISTS " + Tables.SPEAKERS);
      db.execSQL("DROP TABLE IF EXISTS " + Tables.SESSIONS);
      db.execSQL("DROP TABLE IF EXISTS " + Tables.ROOMS);
      db.execSQL("DROP TABLE IF EXISTS " + Tables.TAGS);
      db.execSQL("DROP TABLE IF EXISTS " + Tables.BLOCKS);

      onCreate(db);
      version = CUR_DATABASE_VERSION;
    }

    if (dataInvalidated) {
      LOGD(TAG, "Data invalidated; resetting our data timestamp.");
      ConferenceDataHandler.resetDataTimestamp(mContext);
      if (account != null) {
        LOGI(TAG, "DB upgrade complete. Requesting resync.");
        new SessionApiWebService(mContext).getAllSessions(BuildConfig.SLEEPING_PILL_SLUG_URL);
      }
    }
  }

  public static void deleteDatabase(Context context) {
    context.deleteDatabase(DATABASE_NAME);
  }
}
