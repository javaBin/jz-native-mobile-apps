package no.schedule.javazone.v3.io.model;

import com.google.gson.annotations.SerializedName;

import java.util.Random;

public class Session {
  @SerializedName("sessionId")
  public String id;
  @SerializedName("intendedAudience")
  public String tags;
  public String endTimeZulu;
  public String level;
  public String length;
  public String language;
  @SerializedName("abstract")
  public String description;
  public String published;
  public String video;
  public String title;
  public String room;
  public String conferenceId;
  public String startTimeZulu;
  public Speaker[] speakers;
  public String startTime;
  public String endTime;
  public String slug;
  public transient boolean isLivestream;


  public class RelatedContent {
    public String id;
    public String name;

    @Override
    public String toString() {
      return "RelatedContent{" +
          "id='" + id + '\'' +
          ", name='" + name + '\'' +
          '}';
    }
  }

  public String getImportHashCode() {
    return (new Random()).nextLong()+"";
  }

  public String makeTagsList() {
    int i;
    if (tags == null || tags.length() == 0) return "";

    String splitTags[] = tags.split(",");
    StringBuilder sb = new StringBuilder();
    sb.append(splitTags[0]);
    for (i = 1; i < splitTags.length; i++) {
      sb.append(",").append(splitTags[i]);
    }
    return sb.toString();
  }

  public boolean hasTag(String tag) {
    if (tags == null || tags.length() == 0) return false;

    String splitTags[] = tags.split(",");
    for (String myTag : splitTags) {
      if (myTag.equals(tag)) {
        return true;
      }
    }
    return false;
  }
}
