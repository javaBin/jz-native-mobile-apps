package no.schedule.javazone.v3.io.model;

import com.google.gson.annotations.SerializedName;

import java.util.Random;

import no.schedule.javazone.v3.util.HashUtils;

public class Tag {
  public String id;
  public String tag;
  public String name;
  public String category;
  public String color;
  @SerializedName("abstract")
  public String _abstract;
  public int order_in_category;
  public String photoUrl;

  public String getImportedHashCode() {
    return HashUtils.computeWeakHash(name);
  }

}
