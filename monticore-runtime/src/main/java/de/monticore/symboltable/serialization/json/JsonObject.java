/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

import de.monticore.prettyprint.IndentPrinter;
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
    this.members = new LinkedHashMap<>();
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
    return print(new IndentPrinter());
  }

  //////////////////// Convenience Methods ////////////////////

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
      return getMember(name).getAsJsonNumber().getNumberAsInteger();
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
      return Optional.ofNullable(getMember(name).getAsJsonNumber().getNumberAsInteger());
    }
    return Optional.empty();
  }

  /**
   * Checks whether there exists a member with the corresponding name and checks
   * whether it is of type JSON-Double.
   *
   * @param name The name of the possible JSON element
   * @return true if the correctly typed member available, false otherwise
   */
  public boolean hasDoubleMember(String name) {
    return hasMember(name) && getMember(name).isJsonNumber();
  }

  /**
   * This method returns the value of an Double member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public double getDoubleMember(String name) {
    if (hasIntegerMember(name)) {
      return getMember(name).getAsJsonNumber().getNumberAsDouble();
    }
    Log.error("0xA0578 \"" + name + "\" is not a Json Double member of \"" + this + "\"!");
    return -1;
  }

  /**
   * This method returns an Optional of an Double member of this object, if it exists, Otherwise,
   * returns an empty optional.
   *
   * @param name
   * @return
   */
  public Optional<Double> getDoubleMemberOpt(String name) {
    if (hasIntegerMember(name)) {
      return Optional.ofNullable(getMember(name).getAsJsonNumber().getNumberAsDouble());
    }
    return Optional.empty();
  }

  /**
   * Checks whether there exists a member with the corresponding name and checks
   * whether it is of type JSON-Float.
   *
   * @param name The name of the possible JSON element
   * @return true if the correctly typed member available, false otherwise
   */
  public boolean hasFloatMember(String name) {
    return hasMember(name) && getMember(name).isJsonNumber();
  }

  /**
   * This method returns the value of an Float member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public float getFloatMember(String name) {
    if (hasIntegerMember(name)) {
      return getMember(name).getAsJsonNumber().getNumberAsFloat();
    }
    Log.error("0xA0579 \"" + name + "\" is not a Json Float member of \"" + this + "\"!");
    return -1;
  }

  /**
   * This method returns an Optional of an Double member of this object, if it exists, Otherwise,
   * returns an empty optional.
   *
   * @param name
   * @return
   */
  public Optional<Float> getFloatMemberOpt(String name) {
    if (hasIntegerMember(name)) {
      return Optional.ofNullable(getMember(name).getAsJsonNumber().getNumberAsFloat());
    }
    return Optional.empty();
  }

  /**
   * Checks whether there exists a member with the corresponding name and checks
   * whether it is of type JSON-Long.
   *
   * @param name The name of the possible JSON element
   * @return true if the correctly typed member available, false otherwise
   */
  public boolean hasLongMember(String name) {
    return hasMember(name) && getMember(name).isJsonNumber();
  }

  /**
   * This method returns the value of an Long member of this object, if it exists, Otherwise, raises an error and returns null.
   *
   * @param name
   * @return
   */
  public long getLongMember(String name) {
    if (hasIntegerMember(name)) {
      return getMember(name).getAsJsonNumber().getNumberAsLong();
    }
    Log.error("0xA0565 \"" + name + "\" is not a Json Long member of \"" + this + "\"!");
    return -1;
  }

  /**
   * This method returns an Optional of an Long member of this object, if it exists, Otherwise,
   * returns an empty optional.
   *
   * @param name
   * @return
   */
  public Optional<Long> getLongMemberOpt(String name) {
    if (hasIntegerMember(name)) {
      return Optional.ofNullable(getMember(name).getAsJsonNumber().getNumberAsLong());
    }
    return Optional.empty();
  }

  @Override public String print(IndentPrinter p) {
    boolean indent = JsonPrinter.isIndentationEnabled();
    if (members.isEmpty() && !JsonPrinter.isSerializingDefaults()) {
      return p.getContent();
    }

    // print members of object with a buffer to check whether it is empty
    IndentPrinter buffer = new IndentPrinter();
    buffer.setIndentation(p.getIndentation() + 1);

    // print the value of each member with another buffer to check emptiness
    IndentPrinter tmp = new IndentPrinter();
    tmp.setIndentation(p.getIndentation() + 1);

    String sep = "";
    for (String k : members.keySet()) {
      members.get(k).print(tmp);
      if (!tmp.getContent().isEmpty()) {
        buffer.print(sep + "\"" + k + (indent ? "\": " : "\":") + tmp.getContent());
        tmp.clearBuffer();
        sep = indent ? ",\n" : ",";
      }
    }

    if (!buffer.getContent().isEmpty() || JsonPrinter.isSerializingDefaults()) {
      if (indent) {
        p.println("{");
        p.indent();
        p.print(buffer.getContent());
        p.println();
        p.unindent();
        p.print("}");
      }
      else {
        p.print("{");
        p.print(buffer.getContent());
        p.print("}");
      }
    }

    return p.getContent();
  }

}
