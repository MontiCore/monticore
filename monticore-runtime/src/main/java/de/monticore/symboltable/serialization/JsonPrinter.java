/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

/**
 * Facade for the {@link IndentPrinter} that is capable of printing JSON syntax only. It hides
 * details on the concrete syntax of Json and performs basic well-formedness checks on the
 * produced Json.
 */
public class JsonPrinter {

  protected boolean DEFAULT_BOOLEAN = false;

  protected int DEFAULT_NUMBER = 0;

  protected String DEFAULT_STRING = "";

  protected static boolean enableIndentation = false;

  protected boolean serializeDefaults;

  protected IndentPrinter printer;

  protected boolean isFirstAttribute;

  protected int nestedArrayDepth;

  protected int nestedObjectDepth;

  protected boolean indentBeforeNewLine;

  protected boolean isInEmptyArray = false;

  protected String arrayBeginBuffer;

  protected String objectBeginBuffer;

  /**
   * Constructor for de.monticore.symboltable.serialization.JsonPrinter
   *
   * @param serializeDefaults
   */
  public JsonPrinter(boolean serializeDefaults) {
    this.serializeDefaults = serializeDefaults;
    this.printer = new IndentPrinter();
    this.isFirstAttribute = true;
    this.nestedArrayDepth = 0;
    this.nestedObjectDepth = 0;
    this.indentBeforeNewLine = false;
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
  public boolean isSerializingDefaults() {
    return serializeDefaults;
  }

  /**
   * @param serializeDefaults
   */
  public void setSerializeDefaults(boolean serializeDefaults) {
    this.serializeDefaults = serializeDefaults;
  }

  /**
   * returns true, if the JsonPrinter is in a state in which at least
   * one object has been opened that is not closed yet.
   *
   * @return
   */
  public boolean isInObject() {
    return nestedObjectDepth > 0;
  }

  /**
   * Prints the begin of an object in Json notation.
   */
  public void beginObject() {
    printBufferedBeginArray();
    printCommaIfNecessary();
    print("{");
    isFirstAttribute = true;
    nestedObjectDepth++;
    indentBeforeNewLine = true;
  }

  /**
   * Prints the begin of an object in Json notation as member or the current object.
   */
  public void beginObject(String kind) {
    printCommaIfNecessary();
    print("\"");
    print(kind);
    if (isIndentationEnabled()) {
      print("\": {");
    }
    else {
      print("\":{");
    }
    isFirstAttribute = true;
    nestedObjectDepth++;
    indentBeforeNewLine = true;
  }

  /**
   * Prints the end of an object in Json notation.
   */
  public void endObject() {
    println("");
    print("}");
    if (0 == nestedObjectDepth) {
      isFirstAttribute = true;
    }
    nestedObjectDepth--;
    unindent();
  }

  /**
   * Prints the beginning of a collection in Json notation as member or the current object.
   */
  public void beginArray(String kind) {
    printBufferedBeginArray();
    StringBuilder sb = new StringBuilder();
    if (!isFirstAttribute) {
      sb.append(",");
    }
    else {
      isFirstAttribute = false;
    }
    if (JsonPrinter.isIndentationEnabled()) {
      sb.append("\n");
    }
    sb.append("\"");
    sb.append(kind);
    if (isIndentationEnabled()) {
      sb.append("\": [");
    }
    else {
      sb.append("\":[");
    }
    isFirstAttribute = true;
    nestedArrayDepth++;
    indentBeforeNewLine = true;

    isInEmptyArray = true;
    arrayBeginBuffer = sb.toString();
  }

  /**
   * Prints the beginning of a collection in Json notation.
   */
  public void beginArray() {
    printBufferedBeginArray();
    StringBuilder sb = new StringBuilder();
    if (!isFirstAttribute) {
      sb.append(",");
    }
    else {
      isFirstAttribute = false;
    }
    if (JsonPrinter.isIndentationEnabled()) {
      sb.append("\n");
    }
    sb.append("[");
    isFirstAttribute = true;
    nestedArrayDepth++;
    indentBeforeNewLine = true;

    isInEmptyArray = true;
    arrayBeginBuffer = sb.toString();
  }

  /**
   * Prints the end of a collection in Json notation.
   */
  public void endArray() {
    if (serializeDefaults) {
      printBufferedBeginArray();
    }
    if (!isInEmptyArray) {
      println("");
      print("]");
      isFirstAttribute = false; // This is to handle empty lists
      unindent();
    }
    else if (!serializeDefaults) {
      //restore isFirstAttribute
      isFirstAttribute = !arrayBeginBuffer.startsWith(",");
      arrayBeginBuffer = "";
    }
    nestedArrayDepth--;
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
    if (!values.isEmpty()) {
      beginArray(kind);
      values.stream().forEach(o -> value(o));
      endArray();
    }
    else if (serializeDefaults) {
      beginArray(kind);
      endArray();
    }
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
      internalMember(kind, null);
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
      internalMember(kind, value);
    }
  }
  public void memberNoDef(String kind, double value) {
      internalMember(kind, value);
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
      internalMember(kind, value);
    }
  }
  public void memberNoDef(String kind, long value) {
      internalMember(kind, value);
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
      internalMember(kind, value);
    }
  }
  public void memberNoDef(String kind, float value) {
    internalMember(kind, value);
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
      internalMember(kind, value);
    }
  }
  public void memberNoDef(String kind, int value) {
    internalMember(kind, value);
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
      internalMember(kind, value);
    }
  }
  public void memberNoDef(String kind, boolean value) {
    internalMember(kind, value);
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
      internalMember(kind, "\"" + escapeSpecialChars(value) + "\"");
    }
  }
  public void memberNoDef(String kind, String value) {
    internalMember(kind, "\"" + escapeSpecialChars(value) + "\"");
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
      internalMember(kind, value);
    }
  }
  public void memberJsonNoDef(String kind, String value) {
      internalMember(kind, value);
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
      internalValue(value);
  }

  /**
   * Prints a long as Json value.
   *
   * @param value The long value of the Json attribute
   */
  public void value(long value) {
      internalValue(value);
  }

  /**
   * Prints a float as Json value.
   *
   * @param value The float value of the Json attribute
   */
  public void value(float value) {
      internalValue(value);
  }

  /**
   * Prints an int as Json value, if it deviates from the default boolean value or if
   * * default values should be printed.
   *
   * @param value The int value of the Json attribute
   */
  public void value(int value) {
    internalValue(value);
  }

  /**
   * Prints a boolean value as Json value.
   *
   * @param value The boolean value of the Json attribute
   */
  public void value(boolean value) {
    internalValue(value);
  }

  /**
   * Prints a String as Json value. NOTE: if the parameter value is a serialized String, use the
   * value(JsonPrinter) method instead! Otherwise the content is escaped twice!
   *
   * @param value The String value of the Json attribute
   */
  public void value(String value) {
    internalValue("\"" + escapeSpecialChars(value) + "\"");
  }

  /**
   * Prints a String that contains encoded Json. NOTE: if the parameter value is NOT a serialized
   * String, use the value(String) method instead! Otherwise the content is not escaped properly!
   *
   * @param value The String encoded in JSON
   */
  public void valueJson(String value) {
      internalValue(value);
  }

  /**
   * Prints a Json attribute that is of an object type and therefore needs separat serialization.
   *
   * @param value The JsonPrinter of the value object
   */
  public void value(JsonPrinter value) {
    valueJson(value.getContent());
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

  /**
   * This method is for internal use of this class only. It prints a comma to separate attributes,
   * if the following attribute is not the first one in the current Json object.
   */
  protected void printCommaIfNecessary() {
    if (!isFirstAttribute) {
      println(",");
    }
    else {
      isFirstAttribute = false;
      println("");
    }
    if (indentBeforeNewLine) {
      indent();
      indentBeforeNewLine = false;
    }

  }

  protected void internalMember(String kind, Object value) {
    printCommaIfNecessary();
    print("\"");
    print(kind);
    if (isIndentationEnabled()) {
      print("\": ");
    }
    else {
      print("\":");
    }
    print(value);
  }

  protected void internalValue(Object value) {
    printBufferedBeginArray();
    printCommaIfNecessary();
    print(value);
  }

  protected void printBufferedBeginArray() {
    if (isInEmptyArray) {
      isInEmptyArray = false;
      print(arrayBeginBuffer);
    }
    if (indentBeforeNewLine) {
      indent();
      indentBeforeNewLine = false;
    }
  }

  /**
   * Resets everything printed so far
   */
  public void clearBuffer() {
    this.printer = new IndentPrinter();
    this.isFirstAttribute = true;
    this.nestedArrayDepth = 0;
    this.nestedObjectDepth = 0;
    this.indentBeforeNewLine = false;
    this.isInEmptyArray = false;
  }

  /**
   * Returns the current value of the Json code produced so far And performs basic checks for
   * correct nesting of composed data
   */
  public String getContent() {
    if (0 != nestedArrayDepth) {
      Log.error("0xA0600 Invalid nesting of Json lists in " + toString());
    }
    if (0 != nestedObjectDepth) {
      Log.error("0xA0601 Invalid nesting of Json objects in " + toString());
    }
    // return content of printer without first character, which is a newline
    return toString();
  }

  /**
   * Returns the current value of the Json code produced so far.
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    // return content of printer without first character, which is a newline
    String content = printer.getContent();
    if (content.startsWith("\n")) {
      content = content.substring(1);
    }
    return content;
  }

  /////////////////////////// methods to handle optional pretty printing with line breaks and
  /////////////////////////// indentation ////////////////////////////
  protected void print(Object o) {
    printer.print(o);
  }

  protected void println(Object o) {
    if (JsonPrinter.isIndentationEnabled()) {
      printer.println(o);
    }
    else {
      printer.print(o);
    }
  }

  protected void indent() {
    if (JsonPrinter.isIndentationEnabled()) {
      printer.indent();
    }
  }

  protected void unindent() {
    if (JsonPrinter.isIndentationEnabled()) {
      printer.unindent();
    }
  }

}
