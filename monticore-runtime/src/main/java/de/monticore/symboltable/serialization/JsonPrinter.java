/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import java.util.Collection;
import java.util.Optional;

import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

/**
 * Facade for the {@link IndentPrinter} that is capable of printing JSON syntax only. It hides
 * details on the concrete syntax of Json.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonPrinter {
  
  protected static boolean enableIndentation = false;
  
  protected boolean serializeEmptyLists;
  
  protected IndentPrinter printer;
  
  protected boolean isFirstAttribute;
  
  protected int nestedListDepth;
  
  protected int nestedObjectDepth;
  
  protected boolean indentBeforeNewLine;
  
  /**
   * 
   * Constructor for de.monticore.symboltable.serialization.JsonPrinter
   * @param serializeEmptyLists
   */
  public JsonPrinter(boolean serializeEmptyLists) {
    this.serializeEmptyLists = serializeEmptyLists;
    this.printer = new IndentPrinter();
    this.isFirstAttribute = true;
    this.nestedListDepth = 0;
    this.nestedObjectDepth = 0;
    this.indentBeforeNewLine = false;
  }
  
  /**
   * 
   * Constructor for de.monticore.symboltable.serialization.JsonPrinter that does not print empty lists
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
   * @param enableIndentation the enableIndentation to set
   */
  public static void enableIndentation() {
    JsonPrinter.enableIndentation = true;
  }

  /**
   * @param enableIndentation the enableIndentation to set
   */
  public static void disableIndentation() {
    JsonPrinter.enableIndentation = false;
  }
  
  /**
   * Prints the begin of an object in Json notation.
   */
  public void beginObject() {
    printCommaIfNecessary();
//    println("{");
    print("{");
    isFirstAttribute = true;
    nestedObjectDepth++;
//    indent();
    indentBeforeNewLine = true;
  }
  
  /**
   * Prints the end of an object in Json notation.
   */
  public void endObject() {
    println("");
//    println("}");
    print("}");
    if (0 == nestedListDepth) {
      isFirstAttribute = true;
    }
    nestedObjectDepth--;
    unindent();
  }
  
  /**
   * Prints the beginning of a collection in Json notation. If the optional parameter "kind" is
   * present, it prints the collection as attribute of the given kind.
   */
  public void beginArray(String kind) {
    printCommaIfNecessary();
    print("\"");
    print(kind);
//    println("\":[");
    if(isIndentationEnabled()) {
      print("\": [");
    }
    else {
      print("\":[");
    }
    isFirstAttribute = true;
    nestedListDepth++;
//    indent();
    indentBeforeNewLine = true;
  }
  
  /**
   * Prints the beginning of a collection in Json notation. If the optional parameter "kind" is
   * present, it prints the collection as attribute of the given kind.
   */
  public void beginArray() {
    printCommaIfNecessary();
//    println("[");
    print("[");
    isFirstAttribute = true;
    nestedListDepth++;
//    indent();
    indentBeforeNewLine = true;
  }
  
  /**
   * Prints the end of a collection in Json notation.
   */
  public void endArray() {
    println("");
//    println("]");
    print("]");
    nestedListDepth--;
    isFirstAttribute = false; // This is to handle empty lists
    unindent();
  }
  
  /**
   * Prints a Json collection with the given kind as key and the given collection of object values.
   * Empty lists are serialized only, if serializeEmptyLists() is activated via the constructor. To
   * serialize the passed objects, their toString() method is invoked. Complex objects should be
   * serialized separately, before they are passed as parameter to this method!
   * 
   * @param kind The key of the Json attribute
   * @param values The values of the Json attribute
   */
  public void member(String kind, Collection<String> values) {
    if (!values.isEmpty()) {
      beginArray(kind);
      values.stream().forEach(o -> value(o));
      endArray();
    }
    else if (serializeEmptyLists) {
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
   * @param kind The key of the Json attribute
   * @param value The value of the Json attribute
   */
  public void member(String kind, Optional<String> value) {
    if (null != value && value.isPresent()) {
      member(kind, value.get());
    }
    else if (serializeEmptyLists) {
      internalMember(kind, null);
    }
  }
  /**
   * Prints a Json member with the given kind as key and the given double value, which is a basic
   * data type in Json.
   * 
   * @param kind The key of the Json attribute
   * @param value The double value of the Json attribute
   */
  public void member(String kind, double value) {
    internalMember(kind, value);
  }
  
  /**
   * Prints a Json member with the given kind as key and the given long value, which is a basic data
   * type in Json.
   * 
   * @param kind The key of the Json attribute
   * @param value The long value of the Json attribute
   */
  public void member(String kind, long value) {
    internalMember(kind, value);
  }
  
  /**
   * Prints a Json member with the given kind as key and the given float value, which is a basic
   * data type in Json.
   * 
   * @param kind The key of the Json attribute
   * @param value The float value of the Json attribute
   */
  public void member(String kind, float value) {
    internalMember(kind, value);
  }
  
  /**
   * Prints a Json member with the given kind as key and the given int value, which is a basic data
   * type in Json.
   * 
   * @param kind The key of the Json attribute
   * @param value The int value of the Json attribute
   */
  public void member(String kind, int value) {
    internalMember(kind, value);
  }
  
  /**
   * Prints a Json member with the given kind as key and the given boolean value, which is a basic
   * data type in Json.
   * 
   * @param kind The key of the Json attribute
   * @param value The boolean value of the Json attribute
   */
  public void member(String kind, boolean value) {
    internalMember(kind, value);
  }
  
  /**
   * Prints a Json member with the given kind as key and the given String value, which is a basic
   * data type in Json. NOTE: if the parameter value is a serialized String, use the
   * value(JsonPrinter) method instead! Otherwise escaped symbols are double escaped!
   * 
   * @param kind The key of the Json attribute
   * @param value The boolean value of the Json attribute
   */
  public void member(String kind, String value) {
    internalMember(kind, preprocessString(value));
  }
  
  /**
   * Prints a Json member with the given kind as key and a Json value that is of an object type and
   * therefore needs separat serialization.
   * 
   * @param kind The key of the Json attribute
   * @param value The boolean value of the Json attribute
   */
  public void member(String kind, JsonPrinter value) {
    internalMember(kind, value.getContent());
  }
  
  /**
   * Prints a double as Json value
   * 
   * @param kind The key of the Json attribute
   * @param value The double value of the Json attribute
   */
  public void value(double value) {
    internalValue(value);
  }
  
  /**
   * Prints a long as Json value
   * 
   * @param kind The key of the Json attribute
   * @param value The long value of the Json attribute
   */
  public void value(long value) {
    internalValue(value);
  }
  
  /**
   * Prints a float as Json value
   * 
   * @param kind The key of the Json attribute
   * @param value The float value of the Json attribute
   */
  public void value(float value) {
    internalValue(value);
  }
  
  /**
   * Prints a String as int value
   * 
   * @param kind The key of the Json attribute
   * @param value The int value of the Json attribute
   */
  public void value(int value) {
    internalValue(value);
  }
  
  /**
   * Prints a String as boolean value
   * 
   * @param kind The key of the Json attribute
   * @param value The boolean value of the Json attribute
   */
  public void value(boolean value) {
    internalValue(value);
  }
  
  /**
   * Prints a String as Json value. NOTE: if the parameter value is a serialized String, use the
   * value(JsonPrinter) method instead! Otherwise escaped symbols are double escaped!
   * 
   * @param kind The key of the Json attribute
   * @param value The String value of the Json attribute
   */
  public void value(String value) {
    internalValue(preprocessString(value));
  }
  
  /**
   * Prints a Json attribute that is of an object type and therefore needs separat serialization.
   * 
   * @param kind The key of the Json attribute
   * @param value The JsonPrinter of the value object
   */
  public void value(JsonPrinter value) {
    internalValue(value.getContent());
  }
  
  protected String preprocessString(String string) {
    String s = string.trim();
    boolean isFramedInQuotationMarks = s.length() > 0 && s.startsWith("\"") && s.endsWith("\"");
    boolean isSerializedObject = s.length() > 0 && s.startsWith("{") && s.endsWith("}");
    string = escapeSpecialChars(string);
    if (!isFramedInQuotationMarks && !isSerializedObject) {
      return "\"" + string + "\"";
    }
    else {
      return s;
    }
  }
  
  /**
   * Adds escape sequences for all characters that are escaped in Java Strings according to
   * https://docs.oracle.com/javase/tutorial/java/data/characters.html
   */
  protected String escapeSpecialChars(String input) {
    return input
    .replace("\\", "\\\\")  // Insert a backslash character in the text at this point.
    .replace("\t", "\\t")   // Insert a tab in the text at this point.
    .replace("\b", "\\b")   // Insert a backspace in the text at this point.
    .replace("\n", "\\n")   // Insert a newline in the text at this point.
    .replace("\r", "\\r")   // Insert a carriage return in the text at this point.
    .replace("\f", "\\f")   // Insert a formfeed in the text at this point.
    .replace("\'", "\\\'")  // Insert a single quote character in the text at this point.
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
    if(indentBeforeNewLine) {
      indent();
      indentBeforeNewLine = false;
    }

  }
  
  private void internalMember(String kind, Object value) {
    printCommaIfNecessary();
    print("\"");
    print(kind);
    if(isIndentationEnabled()) {
      print("\": ");
    }
    else {
      print("\":");
    }
    print(value);
  }
  
  private void internalValue(Object value) {
    printCommaIfNecessary();
    print(value);
  }
  
  /**
   * Returns the current value of the Json code produced so far And performs basic checks for correct nesting of composed data
   * 
   */
  public String getContent() {
    if (0 != nestedListDepth) {
      Log.error("0xA0600 Invalid nesting of Json lists in " + printer.getContent());
    }
    if (0 != nestedObjectDepth) {
      Log.error("0xA0601 Invalid nesting of Json objects in " + printer.getContent());
    }
    //return content of printer without first character, which is a newline
    return printer.getContent().substring(1);
  }
  
  /**
   * Returns the current value of the Json code produced so far.
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    //return content of printer without first character, which is a newline
    return printer.getContent().substring(1); 
  }
  
  /////////////////////////// methods to handle optional pretty printing with line breaks and indentation ////////////////////////////
  private void print(Object o) {
    printer.print(o);
  }
  
  private void println(Object o) {
    if(JsonPrinter.isIndentationEnabled()) {
      printer.println(o);
    }
    else {
      printer.print(o);
    }
  }
  
  private void indent() {
    if(JsonPrinter.isIndentationEnabled()) {
      printer.indent();
    }
  }
  
  private void unindent() {
    if(JsonPrinter.isIndentationEnabled()) {
      printer.unindent();
    }
  }
  
}
