package no.schedule.javazone.v3.io.model;

import com.google.gson.annotations.SerializedName;

public class Tag {
  public String tag;
  public String name;
  public String category;
  public String color;
  @SerializedName("abstract")
  public String _abstract;
  public int order_in_category;
  public String photoUrl;
}
