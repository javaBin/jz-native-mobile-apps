package no.schedule.javazone.v3.io.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.api.client.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import no.schedule.javazone.v3.io.JSONHandler;
import no.schedule.javazone.v3.io.model.Room;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.provider.ScheduleContractHelper;

import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class RoomsHandler extends JSONHandler {
  private static final String TAG = makeLogTag(RoomsHandler.class);

  // map from room ID to Room model object
  private HashMap<String, Room> mRooms = new HashMap<>();

  public RoomsHandler(Context context) {
    super(context);
  }

  @Override
  public void process(@NonNull Gson gson, @NonNull JsonElement element) {
    for (Room room : gson.fromJson(element, Room[].class)) {
      mRooms.put(room.id, room);
    }
  }

  @Override
  public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
    Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Rooms.CONTENT_URI);

    // The list of rooms is not large, so for simplicity we delete all of them and repopulate
    list.add(ContentProviderOperation.newDelete(uri).build());
    for (Room room : mRooms.values()) {
      ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(uri);
      builder.withValue(ScheduleContract.Rooms.ROOM_ID, room.id);
      builder.withValue(ScheduleContract.Rooms.ROOM_NAME, room.name);
      //builder.withValue(ScheduleContract.Rooms.ROOM_FLOOR, room.floor);
      list.add(builder.build());
    }
  }

  @Override
  public void process(@NonNull List<Session> sessions) {
    String[] roomNames = new String[]{"Room 1", "Room 2", "Room 3", "Room 4", "Room 5",
        "Room 6", "Room 7", "Room 8", "Workshop A", "Workshop B", "Workshop C", "Workshop D"};

    for(String roomName: roomNames) {
      Room r = new Room();
      r.id = UUID.randomUUID().toString();
      r.name = roomName;
      mRooms.put(roomName, r);
    }
  }
}
