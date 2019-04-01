/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
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
  
  protected boolean serializeEmptyLists;
  
  protected IndentPrinter printer;
  
  protected boolean isFirstAttribute;
  
  protected int nestedListDepth;
  
  protected int nestedObjectDepth;
  
  public JsonPrinter(boolean serializeEmptyLists) {
    this.serializeEmptyLists = serializeEmptyLists;
    this.printer = new IndentPrinter();
    this.isFirstAttribute = true;
    this.nestedListDepth = 0;
    this.nestedObjectDepth = 0;
  }
  
  public JsonPrinter() {
    this(false);
  }
  
  public void beginObject() {
    printCommaIfNecessary();
    printer.print("{");
    isFirstAttribute = true;
    nestedObjectDepth++;
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
  public void attribute(String kind, Collection<?> values) {
    if (!values.isEmpty()) {
      beginAttributeList(kind);
      values.stream().forEach(o -> attribute(o));
      endAttributeList();
    }
    else if (serializeEmptyLists) {
      beginAttributeList(kind);
      endAttributeList();
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
  public void attribute(String kind, Optional<?> value) {
    if (null != value && value.isPresent()) {
      attribute(kind, value.get());
    }
    else if (serializeEmptyLists) {
      basicDataTypeAttribute(kind, null);
    }
  }
  
  public void attribute(Object value) {
    String s = value.toString();
    if (null != s && !"".equals(s)) {
      printCommaIfNecessary();
      printer.print("\"");
      printer.print(s);
      printer.print("\"");
    }
  }
  
  /**
   * Prints a Json attribute with the given kind as key and the given double value, which is a basic
   * data type in Json.
   * 
   * @param kind The key of the Json attribute
   * @param value The double value of the Json attribute
   */
  public void attribute(String kind, double value) {
    basicDataTypeAttribute(kind, value);
  }
  
  /**
   * Prints a Json attribute with the given kind as key and the given long value, which is a basic
   * data type in Json.
   * 
   * @param kind The key of the Json attribute
   * @param value The long value of the Json attribute
   */
  public void attribute(String kind, long value) {
    basicDataTypeAttribute(kind, value);
  }
  
  /**
   * Prints a Json attribute with the given kind as key and the given float value, which is a basic
   * data type in Json.
   * 
   * @param kind The key of the Json attribute
   * @param value The float value of the Json attribute
   */
  public void attribute(String kind, float value) {
    basicDataTypeAttribute(kind, value);
  }
  
  /**
   * Prints a Json attribute with the given kind as key and the given int value, which is a basic
   * data type in Json.
   * 
   * @param kind The key of the Json attribute
   * @param value The int value of the Json attribute
   */
  public void attribute(String kind, int value) {
    basicDataTypeAttribute(kind, value);
  }
  
  /**
   * Prints a Json attribute with the given kind as key and the given boolean value, which is a
   * basic data type in Json.
   * 
   * @param kind The key of the Json attribute
   * @param value The boolean value of the Json attribute
   */
  public void attribute(String kind, boolean value) {
    basicDataTypeAttribute(kind, value);
  }
  
  /**
   * Prints a Json attribute with the given kind as key and the given object value. To serialize the
   * passed object, its toString() method is invoked. Complex objects should be serialized
   * separately, before they are passed as parameter to this method!
   * 
   * @param kind The key of the Json attribute
   * @param value The value of the Json attribute
   */
  public void attribute(String kind, Object value) {
    String s = value.toString().trim();
    boolean isFramedInQuotationMarks = s.length() > 0 && s.startsWith("\"") && s.endsWith("\"");
    boolean isSerializedObject = s.length() > 0 && s.startsWith("{") && s.endsWith("}");
    if (null != s && !"".equals(s)) {
      printCommaIfNecessary();
      printer.print("\"");
      printer.print(kind);
      printer.print("\":");
      if (!isFramedInQuotationMarks && !isSerializedObject) {
        printer.print("\"");
      }
      printer.print(s);
      if (!isFramedInQuotationMarks && !isSerializedObject) {
        printer.print("\"");
      }
    }
  }
  
  /**
   * Prints the beginning of a collection in Json notation. If the optional parameter "kind" is
   * present, it prints the collection as attribute of the given kind.
   */
  public void beginAttributeList(String kind) {
    printCommaIfNecessary();
    printer.print("\"");
    printer.print(kind);
    printer.print("\":[");
    isFirstAttribute = true;
    nestedListDepth++;
  }
  
  /**
   * Prints the beginning of a collection in Json notation. If the optional parameter "kind" is
   * present, it prints the collection as attribute of the given kind.
   */
  public void beginAttributeList() {
    printCommaIfNecessary();
    printer.print("[");
    isFirstAttribute = true;
    nestedListDepth++;
  }
  
  /**
   * Prints the end of a collection in Json notation.
   */
  public void endAttributeList() {
    printer.print("]");
    nestedListDepth--;
    isFirstAttribute = false; // This is to handle empty lists
  }
  
  /**
   * Prints the end of an object in Json notation.
   */
  public void endObject() {
    printer.print("}");
    if (0 == nestedListDepth) {
      isFirstAttribute = true;
    }
    nestedObjectDepth--;
  }
  
  /**
   * This method is for internal use of this class only. It prints a comma to separate attributes,
   * if the following attribute is not the first one in the current Json object.
   */
  protected void printCommaIfNecessary() {
    if (!isFirstAttribute) {
      printer.print(",");
    }
    else {
      isFirstAttribute = false;
    }
  }
  
  private void basicDataTypeAttribute(String kind, Object value) {
    printCommaIfNecessary();
    printer.print("\"");
    printer.print(kind);
    printer.print("\":");
    printer.print(value);
  }
  
  /**
   * Returns the current value of the Json code produced so far.
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    if (0 != nestedListDepth) {
      Log.error("Invalid nesting of Json lists in " + this.toStringWithoutValidation());
    }
    if (0 != nestedObjectDepth) {
      Log.error("Invalid nesting of Json objects in " + this.toStringWithoutValidation());
    }
    return toStringWithoutValidation();
  }
  
  protected String toStringWithoutValidation() {
    return printer.getContent();
  }
  
}
