package no.schedule.javazone.v3.io.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.schedule.javazone.v3.io.JSONHandler;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.provider.ScheduleContractHelper;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class MapPropertyHandler extends JSONHandler {
  public MapPropertyHandler(Context context) {
    super(context);
  }

  @Override
  public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {

  }

  @Override
  public void process(@NonNull List<Session> sessions) {

  }

  @Override
  public void process(@NonNull Gson gson, @NonNull JsonElement element) {

  }
  /*
  private static final String TAG = makeLogTag(MapPropertyHandler.class);

  // maps floor# to tile overlay for that floor
  private HashMap<String, Tile> mTileOverlays = new HashMap<>();

  private String geojson = null;

  public MapPropertyHandler(Context context) {
    super(context);
  }

  @Override
  public void process(@NonNull Gson gson, @NonNull JsonElement element) {
    for (MapData mapData : gson.fromJson(element, MapData[].class)) {
      if (mapData.tiles != null) {
        processTileOverlays(mapData.tiles);
      }
      if (mapData.markers != null) {
        // Get the geojson data that is stored as 'markers' and verify it's valid JSON.
        geojson = mapData.markers.toString();
      }
    }
  }


  public Collection<Tile> getTileOverlays() {
    return mTileOverlays.values();
  }

  private void processTileOverlays(java.util.Map<String, Tile> mapTiles) {
    for (Map.Entry<String, Tile> entry : mapTiles.entrySet()) {
      mTileOverlays.put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
    buildMarkers(list);
    buildTiles(list);
  }

  private void buildMarkers(ArrayList<ContentProviderOperation> list) {
    Uri uri = ScheduleContractHelper
        .setUriAsCalledFromSyncAdapter(ScheduleContract.MapGeoJson.CONTENT_URI);

    list.add(ContentProviderOperation.newDelete(uri).build());

    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(uri);
    builder.withValue(ScheduleContract.MapGeoJson.GEOJSON, geojson);
    list.add(builder.build());
  }

  private void buildTiles(ArrayList<ContentProviderOperation> list) {
    Uri uri = ScheduleContractHelper
        .setUriAsCalledFromSyncAdapter(ScheduleContract.MapTiles.CONTENT_URI);

    list.add(ContentProviderOperation.newDelete(uri).build());

    for (String floor : mTileOverlays.keySet()) {
      Tile tileOverlay = mTileOverlays.get(floor);
      ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(uri);
      builder.withValue(ScheduleContract.MapTiles.TILE_FLOOR, floor);
      builder.withValue(ScheduleContract.MapTiles.TILE_FILE, tileOverlay.filename);
      builder.withValue(ScheduleContract.MapTiles.TILE_URL, tileOverlay.url);
      list.add(builder.build());
    }
  }*/
}