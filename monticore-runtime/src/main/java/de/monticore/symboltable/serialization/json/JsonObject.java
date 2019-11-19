/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

import java.util.*;
import java.util.Map.Entry;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Json Objects contain members in form of key-value pairs. The key is a (unique) String, and the
 * value any JsonElement.
 */
public class JsonObject implements JsonElement {

  protected Map<String, JsonElement> members;

  public JsonObject() {
    this.members = new HashMap<>();
  }

  /**
   * As this is a Json object, return true
   *
   * @return
   */
  @Override
  public boolean isJsonObject() {
    return true;
  }

  /**
   * As this is a Json object, return "this"
   *
   * @return
   */
  @Override
  public JsonObject getAsJsonObject() {
    return this;
  }

  /**
   * A map with all members of this object
   *
   * @return attributes
   */
  public Map<String, JsonElement> getMembers() {
    return this.members;
  }

  /**
   * @param members the attributes to set
   */
  public void setMembers(Map<String, JsonElement> members) {
    this.members = members;
  }

  /**
   * @return
   * @see java.util.Map#size()
   */
  public int sizeMembers() {
    return this.members.size();
  }

  /**
   * @return
   * @see java.util.Map#isEmpty()
   */
  public boolean hasMembers() {
    return this.members.isEmpty();
  }

  /**
   * @param key
   * @return
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  public boolean hasMember(String key) {
    return this.members.containsKey(key);
  }

  /**
   * @param name
   * @return
   * @see java.util.Map#get(java.lang.Object)
   */
  public JsonElement getMember(String name) {
    if (this.members.containsKey(name)) {
      return this.members.get(name);
    }
    Log.error("0xA0570 Member \"" + name + "\" is not contained in Jsob object " + this);
    return null;
  }

  /**
   * @param name
   * @return
   */
  public Optional<JsonElement> getMemberOpt(String name) {
    if (this.members.containsKey(name)) {
      return Optional.of(this.members.get(name));
    }
    return Optional.empty();
  }

  /**
   * @param name
   * @param value
   * @return
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  public JsonElement putMember(String name, JsonElement value) {
    if (null == value) {
      Log.error("0xA0571 Cannot add the member  \"" + "\" to the current Json object \"" + this
          + "\", because its value is null!");
    }
    return this.members.put(name, value);
  }

  /**
   * @param name
   * @return
   * @see java.util.Map#remove(java.lang.Object)
   */
  public JsonElement removeMember(String name) {
    return this.members.remove(name);
  }

  /**
   * @return
   * @see java.util.Map#keySet()
   */
  public Set<String> getMemberNames() {
    return this.members.keySet();
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    for (String s : members.keySet()) {
      if(members.get(s).isJsonString()){
        printer.member(s, members.get(s).toString());
      }
      else{
        printer.memberJson(s, members.get(s).toString());
      }
    }
    printer.endObject();
    return printer.getContent();
  }

  //////////////////// Convenienve Methods ////////////////////

  /**
   * This method returns the value of a String member of this object, if it exists. Otherwise, returns Optional.empty
   *
   * @param name
   * @return
   */
  public Optional<String> getStringMemberOpt(String name) {
    Optional<JsonElement> result = getMemberOpt(name);
    if (result.isPresent() && result.get().isJsonString()) {
      return Optional.of(result.get().getAsJsonString().getValue());
    }
    return Optional.empty();
  }

  /**
   * This method returns the value of a String member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public String getStringMember(String name) {
    Optional<String> result = getStringMemberOpt(name);
    if (result.isPresent()) {
      return result.get();
    }
    else {
      Log.error("0xA0572 \"" + name + "\" is not a Json String member of \"" + this + "\"!");
      return null;
    }
  }

  /**
   * This method returns the value of a Array member of this object, if it exists. Otherwise, returns Optional.empty
   *
   * @param name
   * @return
   */
  public List<JsonElement> getArrayMemberOpt(String name) {
    Optional<JsonElement> result = getMemberOpt(name);
    if (result.isPresent() && result.get().isJsonArray()) {
      return result.get().getAsJsonArray().getValues();
    }
    return new ArrayList<>();
  }

  /**
   * This method returns the value of a Array member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public List<JsonElement> getArrayMember(String name) {
    Optional<JsonElement> result = getMemberOpt(name);
    if (result.isPresent() && result.get().isJsonArray()) {
      return result.get().getAsJsonArray().getValues();
    }
    else {
      Log.error("0xA0573 \"" + name + "\" is not a Json Array member of \"" + this + "\"!");
      return null;
    }
  }

  /**
   * This method returns the value of a Boolean member of this object, if it exists. Otherwise, returns Optional.empty
   *
   * @param name
   * @return
   */
  public Optional<Boolean> getBooleanMemberOpt(String name) {
    Optional<JsonElement> result = getMemberOpt(name);
    if (result.isPresent() && result.get().isJsonBoolean()) {
      return Optional.of(result.get().getAsJsonBoolean().getValue());
    }
    return Optional.empty();
  }

  /**
   * This method returns the value of a Boolean member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public boolean getBooleanMember(String name) {
    Optional<Boolean> result = getBooleanMemberOpt(name);
    if (result.isPresent()) {
      return result.get().booleanValue();
    }
    else {
      Log.error("0xA0574 \"" + name + "\" is not a Json Boolean member of \"" + this + "\"!");
      return false;
    }
  }

  /**
   * This method returns the value of a Object member of this object, if it exists. Otherwise, returns Optional.empty
   *
   * @param name
   * @return
   */
  public Optional<JsonObject> getObjectMemberOpt(String name) {
    Optional<JsonElement> result = getMemberOpt(name);
    if (result.isPresent() && result.get().isJsonObject()) {
      return Optional.of(result.get().getAsJsonObject());
    }
    return Optional.empty();
  }

  /**
   * This method returns the value of a Object member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public JsonObject getObjectMember(String name) {
    Optional<JsonObject> result = getObjectMemberOpt(name);
    if (result.isPresent()) {
      return result.get();
    }
    else {
      Log.error("0xA0575 \"" + name + "\" is not a Json Object member of \"" + this + "\"!");
      return null;
    }
  }

  /**
   * This method returns the value of a Integer member of this object, if it exists. Otherwise, returns Optional.empty
   *
   * @param name
   * @return
   */
  public Optional<Integer> getIntegerMemberOpt(String name) {
    Optional<JsonElement> result = getMemberOpt(name);
    if (result.isPresent() && result.get().isJsonNumber()) {
      return Optional.of(result.get().getAsJsonNumber().getNumberAsInt());
    }
    return Optional.empty();
  }

  /**
   * This method returns the value of a Integer member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public int getIntegerMember(String name) {
    Optional<Integer> result = getIntegerMemberOpt(name);
    if (result.isPresent()) {
      return result.get().intValue();
    }
    else {
      Log.error("0xA0576 \"" + name + "\" is not a Json Integer member of \"" + this + "\"!");
      return -1;
    }
  }

}
