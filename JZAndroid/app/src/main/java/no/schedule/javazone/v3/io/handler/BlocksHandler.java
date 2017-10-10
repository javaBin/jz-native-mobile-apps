package no.schedule.javazone.v3.io.handler;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

import no.schedule.javazone.v3.io.JSONHandler;
import no.schedule.javazone.v3.io.model.Block;
import no.schedule.javazone.v3.io.model.Session;
import no.schedule.javazone.v3.provider.ScheduleContract;
import no.schedule.javazone.v3.provider.ScheduleContractHelper;
import no.schedule.javazone.v3.util.ParserUtils;

import static no.schedule.javazone.v3.util.LogUtils.LOGW;
import static no.schedule.javazone.v3.util.LogUtils.makeLogTag;

public class BlocksHandler extends JSONHandler {
  private static final String TAG = makeLogTag(BlocksHandler.class);
  private ArrayList<Block> mBlocks = new ArrayList<>();

  public BlocksHandler(Context context) {
    super(context);
  }

  @Override
  public void makeContentProviderOperations(ArrayList<ContentProviderOperation> list) {
    Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Blocks.CONTENT_URI);
    list.add(ContentProviderOperation.newDelete(uri).build());
    for (Block block : mBlocks) {
      outputBlock(block, list);
    }
  }

  @Override
  public void process(@NonNull List<Session> sessions) {

  }

  @Override
  public void process(@NonNull Gson gson, @NonNull JsonElement element) {
    for (Block block : gson.fromJson(element, Block[].class)) {
      mBlocks.add(block);
    }
  }

  private static void outputBlock(Block block, ArrayList<ContentProviderOperation> list) {
    Uri uri = ScheduleContractHelper.setUriAsCalledFromServiceApi(
        ScheduleContract.Blocks.CONTENT_URI);
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(uri);
    String title = block.title != null ? block.title : "";
    String subtitle = block.subtitle != null ? block.subtitle : "";

    String type = block.type;
    if ( ! ScheduleContract.Blocks.isValidBlockType(type)) {
      LOGW(TAG, "block from "+block.start+" to "+block.end+" has unrecognized type ("
          +type+"). Using "+ ScheduleContract.Blocks.BLOCK_TYPE_BREAK +" instead.");
      type = ScheduleContract.Blocks.BLOCK_TYPE_BREAK;
    }

    long startTimeL = ParserUtils.parseTime(block.start);
    long endTimeL = ParserUtils.parseTime(block.end);
    final String blockId = ScheduleContract.Blocks.generateBlockId(startTimeL, endTimeL);
    builder.withValue(ScheduleContract.Blocks.BLOCK_ID, blockId);
    builder.withValue(ScheduleContract.Blocks.BLOCK_TITLE, title);
    builder.withValue(ScheduleContract.Blocks.BLOCK_START, startTimeL);
    builder.withValue(ScheduleContract.Blocks.BLOCK_END, endTimeL);
    builder.withValue(ScheduleContract.Blocks.BLOCK_TYPE, type);
    builder.withValue(ScheduleContract.Blocks.BLOCK_SUBTITLE, subtitle);
    list.add(builder.build());
  }
}
