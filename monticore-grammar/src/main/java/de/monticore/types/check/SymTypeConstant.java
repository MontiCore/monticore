/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymTypeConstant extends SymTypeExpression {

  public SymTypeConstant(OOTypeSymbolSurrogate typeSymbolSurrogate) {
    this.typeSymbolSurrogate = typeSymbolSurrogate;
  }

  public String getConstName() {
    return typeSymbolSurrogate.getName();
  }

  public String getBoxedConstName() {
    return box(typeSymbolSurrogate.getName());
  }

  public String getBaseOfBoxedName() {
    String[] parts = box(typeSymbolSurrogate.getName()).split("\\.");
    return parts[parts.length - 1];
  }

  public void setConstName(String constName){
    typeSymbolSurrogate.setName(constName);
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
    return getConstName();
  }

  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonDeSers.KIND, "de.monticore.types.check.SymTypeConstant");
    jp.member("constName", getConstName());
    jp.endObject();
    return jp.getContent();
  }


  /**
   * List of potential constants
   * (on purpose not implemented as enum)
   */
  public static List<String> primitiveTypes = Arrays
      .asList("boolean", "byte", "char", "short", "int", "long", "float", "double", "void");

  /**
   * Map for unboxing const types (e.g. "java.lang.Boolean" -> "boolean")
   */
  public static Map<String, String> unboxMap;

  /**
   * Map for boxing const types (e.g. "boolean" -> "java.lang.Boolean")
   * Results are fully qualified.
   */
  public static Map<String, String> boxMap;


  /**
   * initializing the maps
   */
  static {
    unboxMap = new HashMap<String, String>();
    unboxMap.put("java.lang.Boolean", "boolean");
    unboxMap.put("java.lang.Byte", "byte");
    unboxMap.put("java.lang.Character", "char");
    unboxMap.put("java.lang.Short", "short");
    unboxMap.put("java.lang.Integer", "int");
    unboxMap.put("java.lang.Long", "long");
    unboxMap.put("java.lang.Float", "float");
    unboxMap.put("java.lang.Double", "double");
    unboxMap.put("java.lang.String", "String");
    unboxMap.put("Boolean", "boolean");
    unboxMap.put("Byte", "byte");
    unboxMap.put("Character", "char");
    unboxMap.put("Short", "short");
    unboxMap.put("Integer", "int");
    unboxMap.put("Long", "long");
    unboxMap.put("Float", "float");
    unboxMap.put("Double", "double");

    boxMap = new HashMap<String, String>();
    boxMap.put("boolean", "java.lang.Boolean");
    boxMap.put("byte", "java.lang.Byte");
    boxMap.put("char", "java.lang.Character");
    boxMap.put("double", "java.lang.Double");
    boxMap.put("float", "java.lang.Float");
    boxMap.put("int", "java.lang.Integer");
    boxMap.put("long", "java.lang.Long");
    boxMap.put("short", "java.lang.Short");
    boxMap.put("String", "java.lang.String");
  }

  /**
   * unboxing const types (e.g. "java.lang.Boolean" -> "boolean").
   * otherwise return is unchanged
   *
   * @param boxedName
   * @return
   */
  public static String unbox(String boxedName) {
    if (unboxMap.containsKey(boxedName))
      return unboxMap.get(boxedName);
    else
      return boxedName;
  }


  /**
   * Boxing const types (e.g. "boolean" -> "java.lang.Boolean")
   * Results are fully qualified.
   * Otherwise return is unchanged
   *
   * @param unboxedName
   * @return
   */
  public static String box(String unboxedName) {
    if (boxMap.containsKey(unboxedName))
      return boxMap.get(unboxedName);
    else
      return unboxedName;
  }


  /**
   * Checks whether it is an integer type (incl. byte, long, char)
   *
   * @return true if the given type is an integral type
   */
  public boolean isIntegralType() {
    return "int".equals(getConstName()) ||
        "byte".equals(getConstName()) ||
        "short".equals(getConstName()) ||
        "long".equals(getConstName()) ||
        "char".equals(getConstName());
  }

  /**
   * Checks whether it is an integer type (incl. byte, long, char)
   *
   * @return true if the given type is an integral type
   */
  public boolean isNumericType() {
    return "float".equals(getConstName()) ||
        "double".equals(getConstName()) ||
        isIntegralType();
  }

  /**
   * Am I primitive? (such as "int")
   */
  public boolean isTypeConstant() {
    return true;
  }

  @Override
  public SymTypeConstant deepClone() {
    OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(typeSymbolSurrogate.getName());
    loader.setEnclosingScope(typeSymbolSurrogate.getEnclosingScope());
    SymTypeConstant clone = new SymTypeConstant(loader);
    return clone;
  }


  @Override
  public boolean deepEquals(SymTypeExpression sym){
    if(!(sym instanceof SymTypeConstant)){
      return false;
    }
    SymTypeConstant symCon = (SymTypeConstant) sym;
    if(this.typeSymbolSurrogate== null ||symCon.typeSymbolSurrogate==null){
      return false;
    }
    if(!this.typeSymbolSurrogate.getEnclosingScope().equals(symCon.typeSymbolSurrogate.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbolSurrogate.getName().equals(symCon.typeSymbolSurrogate.getName())){
      return false;
    }
    return this.print().equals(symCon.print());
  }


  // --------------------------------------------------------------------------
}
