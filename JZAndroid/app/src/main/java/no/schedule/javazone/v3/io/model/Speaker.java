package no.schedule.javazone.v3.io.model;

import com.google.gson.annotations.SerializedName;

import no.schedule.javazone.v3.util.HashUtils;

public class Speaker {

  public String id;
  @SerializedName("twitter")
  public String twitterUrl;
  public String pictureId;
  public String pictureUrl;
  public String name;
  public String bio;

  public String getImportHashcode() {
    StringBuilder sb = new StringBuilder();
    sb.append("id").append(id == null ? "" : id)
        .append("twitterUrl").append(twitterUrl == null ? "" : twitterUrl)
        .append("pictureId").append(pictureId == null ? "" : pictureId)
        .append("pictureUrl").append(pictureUrl == null ? "" : pictureUrl)
        .append("name").append(name == null ? "" : name)
        .append("bio").append(bio == null ? "" : bio);
    return HashUtils.computeWeakHash(sb.toString());
  }
}
