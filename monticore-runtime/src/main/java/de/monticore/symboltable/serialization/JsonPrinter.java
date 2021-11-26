/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Function;

import static de.monticore.symboltable.serialization.json.JsonElementFactory.*;

/**
 * Facade for the {@link IndentPrinter} that is capable of printing JSON syntax only. It hides
 * details on the concrete syntax of Json and performs basic well-formedness checks on the
 * produced Json.
 */
public class JsonPrinter {

  public static boolean DEFAULT_BOOLEAN = false;

  public static int DEFAULT_NUMBER = 0;

  public static String DEFAULT_STRING = "";

  protected static boolean enableIndentation = false;

  protected static boolean serializeDefaults = false;

  protected Deque<JsonElement> currElements;

  //denotes the root object or array, otherwise it is empty
  protected Optional<JsonElement> root;

  /**
   * Constructor for de.monticore.symboltable.serialization.JsonPrinter
   *
   * @param serializeDefaults
   */
  public JsonPrinter(boolean serializeDefaults) {
    this.currElements = new ArrayDeque<>();
    this.root = Optional.empty();
    JsonPrinter.serializeDefaults = serializeDefaults;
    JsonElementFactory.setInstance(new JsonElementFactory());
  }

  /**
   * Constructor for de.monticore.symboltable.serialization.JsonPrinter that does not print empty
   * lists by default. This property can be enabled trough { @link #enableIndentation }
   */
  public JsonPrinter() {
    this(false);
  }

  /**
   * @return enableIndentation
   */
  public static boolean isIndentationEnabled() {
    return enableIndentation;
  }

  /**
   * Enables the indentation
   */
  public static void enableIndentation() {
    JsonPrinter.enableIndentation = true;
  }

  /**
   * Disables the indentation
   */
  public static void disableIndentation() {
    JsonPrinter.enableIndentation = false;
  }

  /**
   * @return serializeDefaults
   */
  public static boolean isSerializingDefaults() {
    return serializeDefaults;
  }

  /**
   * @param serializeDefaults
   */
  public static void setSerializeDefaults(boolean serializeDefaults) {
    JsonPrinter.serializeDefaults = serializeDefaults;
  }

  /**
   * Prints the begin of an object in Json notation.
   */
  public void beginObject() {
    if (!currElements.isEmpty() && currElements.peek().isJsonObject()) {
      Log.error("0xA0600 JsonPrinter detected an invalid nesting of Json. "
          + "A JSON object within a JSON object must be a member!");
    }
    else {
      currElements.push(createJsonObject());
      if (!root.isPresent()) {
        root = Optional.of(currElements.peek());
      }
    }
  }

  /**
   * Prints the begin of an object in Json notation as member or the current object.
   */
  public void beginObject(String kind) {
    JsonObject o = createJsonObject();
    getParentObject().putMember(kind, o);
    currElements.push(o);
  }

  /**
   * Prints the end of an object in Json notation.
   */
  public void endObject() {
    if (!currElements.isEmpty() && currElements.peek().isJsonObject()) {
      JsonObject o = currElements.poll().getAsJsonObject();
      addToArray(o);
    }
    else {
      Log.error("0xA0611 JsonPrinter detected an invalid nesting of Json. "
          + "Detected an unexpected end of an object!");
    }
  }

  /**
   * Prints the beginning of a collection in Json notation.
   */
  public void beginArray() {
    if (!currElements.isEmpty() && currElements.peek().isJsonObject()) {
      Log.error("0xA0601 JsonPrinter detected an invalid nesting of Json. "
          + "A JSON array within a JSON object must be a member!");
    }
    else {
      currElements.push(createJsonArray());
      if (!root.isPresent()) {
        root = Optional.of(currElements.peek());
      }
    }
  }

  /**
   * Prints the beginning of a collection in Json notation as member or the current object.
   */
  public void beginArray(String kind) {
    JsonArray a = createJsonArray();
    getParentObject().putMember(kind, a);
    currElements.push(a);
  }

  /**
   * Prints the end of a collection in Json notation.
   */
  public void endArray() {
    if (!currElements.isEmpty() && currElements.peek().isJsonArray()) {
      JsonArray o = currElements.poll().getAsJsonArray();
      addToArray(o);
    }
    else {
      Log.error("0xA0612 JsonPrinter detected an invalid nesting of Json. "
          + "Detected an unexpected end of an array!");
    }
  }

  /**
   * Prints a member with the passed name that is a collection of values as JSON array.
   * The serialization of each value of the collection has to be passed as Function
   *
   * @param name
   * @param values
   * @param printValue
   * @param <T>
   */
  public <T> void array(String name, Collection<T> values, Function<T, String> printValue) {
    if (!values.isEmpty() || serializeDefaults) {
      beginArray(name);
      for (T t : values) {
        valueJson(printValue.apply(t));
      }
      endArray();
    }
  }

  /**
   * Prints a collection of values as JSON array. The serialization of each value of the
   * collection has to be passed as Function
   *
   * @param values
   * @param printValue
   * @param <T>
   */
  public <T> void array(Collection<T> values, Function<T, String> printValue) {
    beginArray();
    for (T t : values) {
      valueJson(printValue.apply(t));
    }
    endArray();
  }

  /**
   * Prints a Json collection with the given kind as key and the given collection of object values.
   * Empty lists are serialized only, if serializeEmptyLists() is activated via the constructor. To
   * serialize the passed objects, their toString() method is invoked. Complex objects should be
   * serialized separately, before they are passed as parameter to this method!
   *
   * @param kind   The key of the Json attribute
   * @param values The values of the Json attribute
   */
  public void member(String kind, Collection<String> values) {
    beginArray(kind);
    values.stream().forEach(o -> value(o));
    endArray();
  }

  /**
   * Prints a Json attribute with the given kind as key and the given optional object value. To
   * serialize the passed object if it is present, its toString() method is invoked. Absent
   * optionals are serialized only, if serializeEmptyLists() is activated via the constructor.
   * Complex objects should be serialized separately, before they are passed as parameter to this
   * method!
   *
   * @param kind  The key of the Json attribute
   * @param value The value of the Json attribute
   */
  public void member(String kind, Optional<String> value) {
    if (null != value && value.isPresent()) {
      member(kind, value.get());
    }
    else if (serializeDefaults) {
      getParentObject().putMember(kind, createJsonNull());
    }
  }

  /**
   * Prints a Json member with the given kind as key and the given double value, which is a basic
   * data type in Json. If the member value equals the default value and the serialization of
   * default values is turned off, the member is not printed.
   *
   * @param kind  The key of the Json attribute
   * @param value The double value of the Json attribute
   */
  public void member(String kind, double value) {
    if (DEFAULT_NUMBER != value || serializeDefaults) {
      memberNoDef(kind, value);
    }
  }

  public void memberNoDef(String kind, double value) {
    JsonElement member = createJsonNumber(String.valueOf(value));
    getParentObject().putMember(kind, member);
  }

  /**
   * Prints a Json member with the given kind as key and the given long value, which is a basic data
   * type in Json. If the member value equals the default value and the serialization of
   * default values is turned off, the member is not printed.
   *
   * @param kind  The key of the Json attribute
   * @param value The long value of the Json attribute
   */
  public void member(String kind, long value) {
    if (DEFAULT_NUMBER != value || serializeDefaults) {
      memberNoDef(kind, value);
    }
  }

  public void memberNoDef(String kind, long value) {
    JsonElement member = createJsonNumber(String.valueOf(value));
    getParentObject().putMember(kind, member);
  }

  /**
   * Prints a Json member with the given kind as key and the given float value, which is a basic
   * data type in Json. If the member value equals the default value and the serialization of
   * default values is turned off, the member is not printed.
   *
   * @param kind  The key of the Json attribute
   * @param value The float value of the Json attribute
   */
  public void member(String kind, float value) {
    if (DEFAULT_NUMBER != value || serializeDefaults) {
      memberNoDef(kind, value);
    }
  }

  public void memberNoDef(String kind, float value) {
    JsonElement member = createJsonNumber(String.valueOf(value));
    getParentObject().putMember(kind, member);
  }

  /**
   * Prints a Json member with the given kind as key and the given int value, which is a basic data
   * type in Json. If the member value equals the default value and the serialization of
   * default values is turned off, the member is not printed.
   *
   * @param kind  The key of the Json attribute
   * @param value The int value of the Json attribute
   */
  public void member(String kind, int value) {
    if (DEFAULT_NUMBER != value || serializeDefaults) {
      memberNoDef(kind, value);
    }
  }

  public void memberNoDef(String kind, int value) {
    JsonElement member = createJsonNumber(String.valueOf(value));
    getParentObject().putMember(kind, member);
  }

  /**
   * Prints a Json member with the given kind as key and the given boolean value, which is a basic
   * data type in Json. If the member value equals the default value and the serialization of
   * default values is turned off, the member is not printed.
   *
   * @param kind  The key of the Json attribute
   * @param value The boolean value of the Json attribute
   */
  public void member(String kind, boolean value) {
    if (DEFAULT_BOOLEAN != value || serializeDefaults) {
      memberNoDef(kind, value);
    }
  }

  public void memberNoDef(String kind, boolean value) {
    JsonElement member = createJsonNumber(String.valueOf(value));
    getParentObject().putMember(kind, member);
  }

  /**
   * Prints a Json member with the given kind as key and the given String value, which is a basic
   * data type in Json. If the member value equals the default value and the serialization of
   * default values is turned off, the member is not printed. NOTE: if the parameter value is a
   * serialized String, use the member(String kind, JsonPrinter value) method or the
   * memberJson(String kind, String value) method instead! Otherwise the content is escaped twice!
   *
   * @param kind  The key of the Json attribute
   * @param value The boolean value of the Json attribute
   */
  public void member(String kind, String value) {
    if (DEFAULT_STRING != value || serializeDefaults) {
      memberNoDef(kind, value);
    }
  }

  public void memberNoDef(String kind, String value) {
    String escValue = escapeSpecialChars(value);
    JsonElement member = createJsonString("\"" + escValue + "\"");
    getParentObject().putMember(kind, member);
  }

  /**
   * Prints a Json member with the given kind as key and the given String value that is encoded
   * in JSON. If the member value equals the default value and the serialization of default values
   * is turned off, the member is not printed.NOTE: if the parameter value is NOT a serialized
   * String, use the member(String kind, String value) method instead! Otherwise the content is not
   * escaped!
   *
   * @param kind
   * @param value
   */
  public void memberJson(String kind, String value) {
    if (DEFAULT_STRING != value || serializeDefaults) {
      memberJsonNoDef(kind, value);
    }
  }

  public void memberJsonNoDef(String kind, String value) {
    JsonElement member = createJsonString(value);
    getParentObject().putMember(kind, member);
  }

  /**
   * Prints a Json member with the given kind as key and a Json value that is of an object type and
   * therefore needs separat serialization.
   *
   * @param kind  The key of the Json attribute
   * @param value The boolean value of the Json attribute
   */
  public void member(String kind, JsonPrinter value) {
    memberJson(kind, value.getContent());
  }

  /**
   * Prints a double as Json value.
   *
   * @param value The double value of the Json attribute
   */
  public void value(double value) {
    intenalNumberValue(String.valueOf(value));
  }

  /**
   * Prints a long as Json value.
   *
   * @param value The long value of the Json attribute
   */
  public void value(long value) {
    intenalNumberValue(String.valueOf(value));
  }

  /**
   * Prints a float as Json value.
   *
   * @param value The float value of the Json attribute
   */
  public void value(float value) {
    intenalNumberValue(String.valueOf(value));
  }

  /**
   * Prints an int as Json value, if it deviates from the default boolean value or if
   * * default values should be printed.
   *
   * @param value The int value of the Json attribute
   */
  public void value(int value) {
    intenalNumberValue(String.valueOf(value));
  }

  /**
   * Prints a boolean value as Json value.
   *
   * @param value The boolean value of the Json attribute
   */
  public void value(boolean value) {
    if (!currElements.isEmpty() && currElements.peek().isJsonArray()) {
      JsonArray parent = currElements.peek().getAsJsonArray();
      parent.add(createJsonBoolean(value));
    }
    else if (!currElements.isEmpty() && currElements.peek().isJsonObject()) {
      Log.error("0xA0606 JsonPrinter detected an invalid nesting of Json. "
          + "Cannot add a numeric value `" + value + "` to the JSON object: `"
          + toString() + "`");
    }
    else {
      currElements.push(createJsonBoolean(value));
    }
  }

  /**
   * Prints a String as Json value. NOTE: if the parameter value is a serialized String, use the
   * value(JsonPrinter) method instead! Otherwise the content is escaped twice!
   *
   * @param value The String value of the Json attribute
   */
  public void value(String value) {
    intenalStringValue("\"" +escapeSpecialChars(value) +"\"");
  }

  /**
   * Prints a String that contains encoded Json. NOTE: if the parameter value is NOT a serialized
   * String, use the value(String) method instead! Otherwise the content is not escaped properly!
   *
   * @param value The String encoded in JSON
   */
  public void valueJson(String value) {
    intenalStringValue(value);
  }

  /**
   * Prints a Json attribute that is of an object type and therefore needs separat serialization.
   *
   * @param value The JsonPrinter of the value object
   */
  public void value(JsonPrinter value) {
    intenalStringValue(value.getContent());
  }

  ////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Adds escape sequences for all characters that are escaped in Java Strings according to
   * https://docs.oracle.com/javase/tutorial/java/data/characters.html
   */
  protected String escapeSpecialChars(String input) {
    return input
        .replace("\\", "\\\\") // Insert a backslash character in the text at this point.
        .replace("\t", "\\t") // Insert a tab in the text at this point.
        .replace("\b", "\\b") // Insert a backspace in the text at this point.
        .replace("\n", "\\n") // Insert a newline in the text at this point.
        .replace("\r", "\\r") // Insert a carriage return in the text at this point.
        .replace("\f", "\\f") // Insert a formfeed in the text at this point.
        .replace("\'", "\\\'") // Insert a single quote character in the text at this point.
        .replace("\"", "\\\""); // Insert a double quote character in the text at this point.
  }

  protected void intenalNumberValue(String value) {
    if (!currElements.isEmpty() && currElements.peek().isJsonArray()) {
      JsonArray parent = currElements.peek().getAsJsonArray();
      parent.add(createJsonNumber(String.valueOf(value)));
    }
    else if (!currElements.isEmpty() && currElements.peek().isJsonObject()) {
      Log.error("0xA0604 JsonPrinter detected an invalid nesting of Json. "
          + "Cannot add a numeric value `" + value + "` to the JSON object: `"
          + toString() + "`");
    }
    else {
      currElements.push(createJsonNumber(String.valueOf(value)));
    }
  }

  protected void intenalStringValue(String value) {
    if (!currElements.isEmpty() && currElements.peek().isJsonArray()) {
      JsonArray parent = currElements.peek().getAsJsonArray();
      parent.add(createJsonString(value));
    }
    else if (!currElements.isEmpty() && currElements.peek().isJsonObject()) {
      Log.error("0xA0603 JsonPrinter detected an invalid nesting of Json. "
          + "Cannot add a String value `" + value + "` to the JSON object: `"
          + toString() + "`");
    }
    else {
      currElements.push(createJsonString(value));
    }
  }

  private JsonObject getParentObject() {
    if (currElements.isEmpty()) {
      Log.error("0xA0613 JsonPrinter detected an invalid nesting of Json. "
          + "Cannot add a member as the first element of a Json String!");
    }
    if (currElements.peek().isJsonObject()) {
      return currElements.peek().getAsJsonObject();
    }
    IndentPrinter p = new IndentPrinter();
    currElements.peek().print(p);
    Log.error("0xA0602 JsonPrinter detected an invalid nesting of Json. "
        + "Cannot add a child member to: `" + p.toString() + "`");
    return createJsonObject();
  }

  public void addToArray(JsonElement e){
    if (!currElements.isEmpty() && currElements.peek().isJsonArray()) {
      JsonArray parent = currElements.peek().getAsJsonArray();
      parent.add(e);
    }
    else if (currElements.isEmpty() || currElements.peek().isJsonObject()) {
      // no op here, because nested objects are handled elsewhere and
      // it is not an error if a JsonObject or JsonArray is the "outermost"
      // JSON element
    }
    else {
      Log.error("0xA0653 JsonPrinter detected an invalid nesting of Json arrays. "
          + "Cannot add `" + e + "` to the JSON : `"
          + toString() + "`");
    }
  }

  /**
   * Resets everything printed so far
   */
  public void clearBuffer() {
    this.currElements = new ArrayDeque<>();
    this.root = Optional.empty();
  }

  /**
   * Returns the current value of the Json code produced so far and performs basic
   * checking for correct nestings
   */
  public String getContent() {
    if (currElements.size() > 1 || (currElements.size() == 1 && root.isPresent())) {
      Log.error("0xA0615 JsonPrinter detected an invalid nesting of Json.");
    }
    return toString();
  }

  /**
   * Returns the current value of the Json code produced so far.
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (currElements.isEmpty()) {
      if (!root.isPresent()) {
        return "";
      }
      IndentPrinter p = new IndentPrinter();
      root.get().print(p);
      return p.getContent();
    }
    else {
      IndentPrinter p = new IndentPrinter();
      currElements.peek().print(p);
      return p.getContent();
    }
  }

}
