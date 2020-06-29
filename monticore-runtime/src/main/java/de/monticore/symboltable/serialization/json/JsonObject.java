/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

import de.monticore.symboltable.serialization.JsonPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * Json Objects contain members in form of name-value pairs. The name is a (unique) String, and the
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
   * @param name
   * @return
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  public boolean hasMember(String name) {
    return this.members.containsKey(name);
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
    Log.error("0xA0570 Member \"" + name + "\" is not contained in Json object " + this);
    return null;
  }

  /**
   * @param name
   * @param value
   * @return
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  public JsonElement putMember(String name, JsonElement value) {
    if (null == value) {
      Log.error(
          "0xA0571 Cannot add the member \"" + name + "\" to the current Json object \"" + this
              + "\", because its value is null!");
      return null;
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
      if (members.get(s).isJsonString()) {
        printer.member(s, members.get(s).toString());
      }
      else {
        printer.memberJson(s, members.get(s).toString());
      }
    }
    printer.endObject();
    return printer.getContent();
  }

  //////////////////// Convenienve Methods ////////////////////

  /**
   * Checks whether there exists a member with the corresponding name and checks
   * whether it is of type String.
   *
   * @param name The name of the possible JSON element
   * @return true if the correctly typed member available, false otherwise
   */
  public boolean hasStringMember(String name) {
    return hasMember(name) && getMember(name).isJsonString();
  }

  /**
   * This method returns the value of a String member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public String getStringMember(String name) {
    if (hasStringMember(name)) {
      return getMember(name).getAsJsonString().getValue();
    }
    Log.error("0xA0572 \"" + name + "\" is not a Json String member of \"" + this + "\"!");
    return null;
  }

  /**
   * This method returns an Optional of a String member of this object, if it exists, Otherwise,
   * returns an empty optional.
   *
   * @param name
   * @return
   */
  public Optional<String> getStringMemberOpt(String name) {
    if (hasStringMember(name)) {
      return Optional.ofNullable(getMember(name).getAsJsonString().getValue());
    }
    return Optional.empty();
  }

  /**
   * Checks whether there exists a member with the corresponding name and checks
   * whether it is of type JSON-Array.
   *
   * @param name The name of the possible JSON element
   * @return true if the correctly typed member available, false otherwise
   */
  public boolean hasArrayMember(String name) {
    return hasMember(name) && getMember(name).isJsonArray();
  }

  /**
   * This method returns the value of an Array member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public List<JsonElement> getArrayMember(String name) {
    if (hasArrayMember(name)) {
      return getMember(name).getAsJsonArray().getValues();
    }
    Log.error("0xA0573 \"" + name + "\" is not a Json Array member of \"" + this + "\"!");
    return null;
  }

  /**
   * This method returns an Optional of an Array member of this object, if it exists, Otherwise,
   * returns an empty optional.
   *
   * @param name
   * @return
   */
  public Optional<List<JsonElement>> getArrayMemberOpt(String name) {
    if (hasArrayMember(name)) {
      return Optional.ofNullable(getMember(name).getAsJsonArray().getValues());
    }
    return Optional.empty();
  }

  /**
   * Checks whether there exists a member with the corresponding name and checks
   * whether it is of type JSON-Boolean.
   *
   * @param name The name of the possible JSON element
   * @return true if the correctly typed member available, false otherwise
   */
  public boolean hasBooleanMember(String name) {
    return hasMember(name) && getMember(name).isJsonBoolean();
  }

  /**
   * This method returns the value of a Boolean member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public boolean getBooleanMember(String name) {
    if (hasBooleanMember(name)) {
      return getMember(name).getAsJsonBoolean().getValue();
    }
    Log.error("0xA0574 \"" + name + "\" is not a Json Boolean member of \"" + this + "\"!");
    return false;
  }

  /**
   * This method returns an Optional of a Boolean member of this object, if it exists, Otherwise,
   * returns an empty optional.
   *
   * @param name
   * @return
   */
  public Optional<Boolean> getBooleanMemberOpt(String name) {
    if (hasBooleanMember(name)) {
      return Optional.ofNullable(getMember(name).getAsJsonBoolean().getValue());
    }
    return Optional.empty();
  }

  /**
   * Checks whether there exists a member with the corresponding name and checks
   * whether it is of type JSON-Object.
   *
   * @param name The name of the possible JSON element
   * @return true if the correctly typed member available, false otherwise
   */
  public boolean hasObjectMember(String name) {
    return hasMember(name) && getMember(name).isJsonObject();
  }

  /**
   * This method returns the value of an Object member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public JsonObject getObjectMember(String name) {
    if (hasObjectMember(name)) {
      return getMember(name).getAsJsonObject();
    }
    Log.error("0xA0575 \"" + name + "\" is not a Json Object member of \"" + this + "\"!");
    return null;
  }

  /**
   * This method returns an Optional of an Object member of this object, if it exists, Otherwise,
   * returns an empty optional.
   *
   * @param name
   * @return
   */
  public Optional<JsonObject> getObjectMemberOpt(String name) {
    if (hasObjectMember(name)) {
      return Optional.ofNullable(getMember(name).getAsJsonObject());
    }
    return Optional.empty();
  }

  /**
   * Checks whether there exists a member with the corresponding name and checks
   * whether it is of type JSON-Integer.
   *
   * @param name The name of the possible JSON element
   * @return true if the correctly typed member available, false otherwise
   */
  public boolean hasIntegerMember(String name) {
    return hasMember(name) && getMember(name).isJsonNumber();
  }

  /**
   * This method returns the value of an Integer member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public int getIntegerMember(String name) {
    if (hasIntegerMember(name)) {
      return getMember(name).getAsJsonNumber().getNumberAsInt();
    }
    Log.error("0xA0576 \"" + name + "\" is not a Json Integer member of \"" + this + "\"!");
    return -1;
  }

  /**
   * This method returns an Optional of an Integer member of this object, if it exists, Otherwise,
   * returns an empty optional.
   *
   * @param name
   * @return
   */
  public Optional<Integer> getIntegerMemberOpt(String name) {
    if (hasIntegerMember(name)) {
      return Optional.ofNullable(getMember(name).getAsJsonNumber().getNumberAsInt());
    }
    return Optional.empty();
  }

}
