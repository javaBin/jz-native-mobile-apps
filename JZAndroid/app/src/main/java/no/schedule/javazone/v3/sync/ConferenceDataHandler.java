package no.schedule.javazone.v3.sync;


import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.turbomanage.httpclient.ConsoleRequestLogger;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.RequestLogger;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import no.schedule.javazone.v3.io.JSONHandler;
import no.schedule.javazone.v3.io.handler.BlocksHandler;
import no.schedule.javazone.v3.io.handler.CardHandler;
import no.schedule.javazone.v3.io.handler.MapPropertyHandler;
import no.schedule.javazone.v3.io.handler.RoomsHandler;
import no.schedule.javazone.v3.io.handler.SearchSuggestHandler;
import no.schedule.javazone.v3.io.handler.SessionsHandler;
import no.schedule.javazone.v3.io.handler.SpeakersHandler;
import no.schedule.javazone.v3.io.handler.TagsHandler;
import no.schedule.javazone.v3.io.model.Room;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.io.model.Speaker;
import no.schedule.javazone.v3.provider.ScheduleContract;

import static no.schedule.javazone.v3.util.LogUtils.LOGD;
import static no.schedule.javazone.v3.util.LogUtils.LOGE;
import static no.schedule.javazone.v3.util.LogUtils.LOGI;
import static no.schedule.javazone.v3.util.LogUtils.LOGW;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

/**
 * Helper class that parses conference data and imports them into the app's
 * Content Provider.
 */
public class ConferenceDataHandler {
  private static final String TAG = makeLogTag(ConferenceDataHandler.class);

  // Shared settings_prefs key under which we store the timestamp that corresponds to
  // the data we currently have in our content provider.
  private static final String SP_KEY_DATA_TIMESTAMP = "data_timestamp";

  // symbolic timestamp to use when we are missing timestamp data (which means our data is
  // really old or nonexistent)
  private static final String DEFAULT_TIMESTAMP = "Sat, 1 Jan 2000 00:00:00 GMT";

  private static final String DATA_KEY_ROOMS = "rooms";
  private static final String DATA_KEY_BLOCKS = "blocks";
  private static final String DATA_KEY_CARDS = "cards";
  private static final String DATA_KEY_TAGS = "tags";
  private static final String DATA_KEY_SPEAKERS = "speakers";
  private static final String DATA_KEY_SESSIONS = "sessions";
  private static final String DATA_KEY_SEARCH_SUGGESTIONS = "search_suggestions";
  private static final String DATA_KEY_MAP = "map";

  private static final String[] DATA_KEYS_IN_ORDER = {
      DATA_KEY_ROOMS,
      DATA_KEY_BLOCKS,

      DATA_KEY_MAP,
  };

  private static final String[] KEYS_PROCESS = {
      DATA_KEY_CARDS,
      DATA_KEY_TAGS,
      DATA_KEY_SPEAKERS,
      DATA_KEY_SESSIONS,
      DATA_KEY_SEARCH_SUGGESTIONS
  };

  private final Context mContext;
  private final Gson mGson = new Gson();

  // Handlers for each entity type:

  private RoomsHandler mRoomsHandler;
  private BlocksHandler mBlocksHandler;
  private SessionsHandler mSessionsHandler;
  private CardHandler mCardHandler;
  private TagsHandler mTagsHandler;
  private SpeakersHandler mSpeakersHandler;
  private SearchSuggestHandler mSearchSuggestHandler;
  private MapPropertyHandler mMapPropertyHandler;



  // Convenience map that maps the key name to its corresponding handler (e.g.
  // "blocks" to mBlocksHandler (to avoid very tedious if-elses)
  private final HashMap<String, JSONHandler> mHandlerForKeyDataBootStrap = new HashMap<>();

  private final HashMap<String, JSONHandler> mHandlerForKeyConference = new HashMap<>();

  // Tally of total content provider operations we carried out (for statistical purposes)
  private int mContentProviderOperationsDone = 0;

  public ConferenceDataHandler(Context ctx) {
    mContext = ctx;
  }

  public void applyConferenceData(List<Session> sessions) {
    LOGD(TAG, "Applying data from " + sessions.size() + " sessions");

    // Create Handlers for each data type
    mHandlerForKeyConference.put(DATA_KEY_ROOMS, mRoomsHandler = new RoomsHandler(mContext));
    mHandlerForKeyConference.put(DATA_KEY_SESSIONS, mSessionsHandler = new SessionsHandler(mContext));

    mHandlerForKeyConference.put(DATA_KEY_SEARCH_SUGGESTIONS, mSearchSuggestHandler =
        new SearchSuggestHandler(mContext));


    processData(sessions);


    // mSessionsHandler.setTagMap(mTagsHandler.getTagMap());
    // mSessionsHandler.setSpeakerMap(mSpeakersHandler.getSpeakerMap());

  }


  /**
   * Parses the conference data in the given objects and imports the data into the
   * content provider. The format of the data is documented at https://code.google.com/p/iosched.
   *
   * @param dataBodies       The collection of JSON objects to parse and import.
   * @param downloadsAllowed Whether or not we are supposed to download data from the internet if
   *                         needed.
   * @throws IOException If there is a problem parsing the data.
   */
  public void applyConferenceData(String[] dataBodies,
                                  boolean downloadsAllowed) throws IOException {
    LOGD(TAG, "Applying data from " + dataBodies.length + " files");

    // create handlers for each data type

    mHandlerForKeyDataBootStrap.put(DATA_KEY_ROOMS, mRoomsHandler = new RoomsHandler(mContext));
    mHandlerForKeyDataBootStrap.put(DATA_KEY_BLOCKS, mBlocksHandler = new BlocksHandler(mContext));
    mHandlerForKeyDataBootStrap.put(DATA_KEY_SEARCH_SUGGESTIONS, mSearchSuggestHandler =
        new SearchSuggestHandler(mContext));
    mHandlerForKeyDataBootStrap.put(DATA_KEY_MAP, mMapPropertyHandler = new MapPropertyHandler(mContext));

    LOGD(TAG, "Processing " + dataBodies.length + " JSON objects.");
    for (int i = 0; i < dataBodies.length; i++) {
      LOGD(TAG, "Processing json object #" + (i + 1) + " of " + dataBodies.length);
      processDataBody(dataBodies[i]);
    }

    // produce the necessary content provider operations
    ArrayList<ContentProviderOperation> batch = new ArrayList<>();
    for (String key : DATA_KEYS_IN_ORDER) {
      LOGI(TAG, "Building content provider operations for: " + key);
      mHandlerForKeyDataBootStrap.get(key).makeContentProviderOperations(batch);
      LOGI(TAG, "Content provider operations so far: " + batch.size());
    }
    LOGD(TAG, "Total content provider operations: " + batch.size());

    // download or process local map tile overlay files (SVG files)
    LOGD(TAG, "Processing map overlay files");
   // processMapOverlayFiles(mMapPropertyHandler.getTileOverlays(), downloadsAllowed);

    // finally, push the changes into the Content Provider
    LOGI(TAG, "Applying " + batch.size() + " content provider operations.");

    try {
      int operations = batch.size();
      if (operations > 0) {
        mContext.getContentResolver().applyBatch(ScheduleContract.CONTENT_AUTHORITY, batch);
      }
      LOGD(TAG, "Successfully applied " + operations + " content provider operations.");
      mContentProviderOperationsDone += operations;
    } catch (RemoteException ex) {
      LOGE(TAG, "RemoteException while applying content provider operations.");
      throw new RuntimeException("Error executing content provider batch operation", ex);
    } catch (OperationApplicationException ex) {
      LOGE(TAG, "OperationApplicationException while applying content provider operations.");
      throw new RuntimeException("Error executing content provider batch operation", ex);
    }


    // notify all top-level paths
    LOGD(TAG, "Notifying changes on all top-level paths on Content Resolver.");
    ContentResolver resolver = mContext.getContentResolver();
    for (String path : ScheduleContract.TOP_LEVEL_PATHS) {
      Uri uri = ScheduleContract.BASE_CONTENT_URI.buildUpon().appendPath(path).build();
      resolver.notifyChange(uri, null);
    }


    LOGD(TAG, "Done applying conference data.");
  }

  public int getContentProviderOperationsDone() {
    return mContentProviderOperationsDone;
  }

  private void processDataBody(String dataBody) throws IOException {
    JsonReader reader = new JsonReader(new StringReader(dataBody));
    JsonParser parser = new JsonParser();
    try {
      reader.setLenient(true); // To err is human

      // the whole file is a single JSON object
      reader.beginObject();

      while (reader.hasNext()) {

        final String key = reader.nextName();
        final JSONHandler handler = mHandlerForKeyDataBootStrap.get(key);
        if (handler != null) {
          LOGD(TAG, "Processing key in conference data json: " + key);
          // pass the value to the corresponding handler
          handler.process(mGson, parser.parse(reader));
        } else {
          LOGW(TAG, "Skipping unknown key in conference data json: " + key);
          reader.skipValue();
        }
      }
      reader.endObject();
    } finally {
      reader.close();
    }
  }

  private void processData(List<Session> sessions) {
    List<Speaker> allSpeakers;
    List<Room> allRooms;


    for(String key: KEYS_PROCESS) {
      final JSONHandler handler = mHandlerForKeyConference.get(key);
      if(handler != null) {
        LOGD(TAG, "Processing key in conference data json: " + key);
        handler.process(sessions);
      } else {
        LOGW(TAG, "Skipping unknown key in conference data json: " + key);
      }
    }
  }




  /**
   * Synchronise the map overlay files either from the local assets (if available) or from a
   * remote url.
   *
   * @param collection Set of tiles containing a local filename and remote url.
   */
  /*
  private void processMapOverlayFiles(Collection<Tile> collection, boolean downloadAllowed)
      throws IOException, SVGParseException {
    // clear the tile cache on disk if any tiles have been updated
    boolean shouldClearCache = false;
    // keep track of used files, unused files are removed
    ArrayList<String> usedTiles = new ArrayList<>();
    for (Tile tile : collection) {
      final String filename = tile.filename;
      final String url = tile.url;

      usedTiles.add(filename);

      if (!MapUtils.hasTile(mContext, filename)) {
        shouldClearCache = true;
        // copy or download the tile if it is not stored yet
        if (MapUtils.hasTileAsset(mContext, filename)) {
          // file already exists as an asset, copy it
          MapUtils.copyTileAsset(mContext, filename);
        } else if (downloadAllowed && !TextUtils.isEmpty(url)) {
          try {
            // download the file only if downloads are allowed and url is not empty
            File tileFile = MapUtils.getTileFile(mContext, filename);
            BasicHttpClient httpClient = new BasicHttpClient();
            httpClient.setRequestLogger(mQuietLogger);
            IOUtils.authorizeHttpClient(mContext, httpClient);
            HttpResponse httpResponse = httpClient.get(url, null);
            IOUtils.writeToFile(httpResponse.getBody(), tileFile);

            // ensure the file is valid SVG
            InputStream is = new FileInputStream(tileFile);
            SVG svg = new SVGBuilder().readFromInputStream(is).build();
            is.close();
          } catch (IOException ex) {
            LOGE(TAG, "FAILED downloading map overlay tile " + url +
                ": " + ex.getMessage(), ex);
          } catch (SVGParseException ex) {
            LOGE(TAG, "FAILED parsing map overlay tile " + url +
                ": " + ex.getMessage(), ex);
          }
        } else {
          LOGD(TAG, "Skipping download of map overlay tile" +
              " (since downloadsAllowed=false)");
        }
      }
    }

    if (shouldClearCache) {
      MapUtils.clearDiskCache(mContext);
    }

    MapUtils.removeUnusedTiles(mContext, usedTiles);
  }
  */

  // Returns the timestamp of the data we have in the content provider.
  public String getDataTimestamp() {
    return PreferenceManager.getDefaultSharedPreferences(mContext).getString(
        SP_KEY_DATA_TIMESTAMP, DEFAULT_TIMESTAMP);
  }

  // Sets the timestamp of the data we have in the content provider.
  public void setDataTimestamp(String timestamp) {
    LOGD(TAG, "Setting data timestamp to: " + timestamp);
    PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString(
        SP_KEY_DATA_TIMESTAMP, timestamp).apply();
  }

  // Reset the timestamp of the data we have in the content provider
  public static void resetDataTimestamp(final Context context) {
    LOGD(TAG, "Resetting data timestamp to default (to invalidate our synced data)");
    PreferenceManager.getDefaultSharedPreferences(context).edit().remove(
        SP_KEY_DATA_TIMESTAMP).apply();
  }

  /**
   * A type of ConsoleRequestLogger that does not log requests and responses.
   */
  private final RequestLogger mQuietLogger = new ConsoleRequestLogger() {
    @Override
    public void logRequest(HttpURLConnection uc, Object content) throws IOException {
    }

    @Override
    public void logResponse(HttpResponse res) {
    }
  };

}

