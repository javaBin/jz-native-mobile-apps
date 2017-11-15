package no.schedule.javazone.v3.io.model;

import no.schedule.javazone.v3.util.HashUtils;

public class Room {
  public String id;
  public String name;

  public String getImportedHashCode() {
    return HashUtils.computeWeakHash(name);
  }
}
