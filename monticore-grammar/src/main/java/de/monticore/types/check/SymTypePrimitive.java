/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class SymTypePrimitive extends SymTypeExpression {

  public SymTypePrimitive(TypeSymbol typeSymbol) {
    this.typeSymbol = typeSymbol;
  }

  public String getPrimitiveName() {
    return typeSymbol.getName();
  }

  public String getBoxedPrimitiveName() {
    return box(typeSymbol.getName());
  }

  public String getBaseOfBoxedName() {
    String[] parts = box(typeSymbol.getName()).split("\\.");
    return parts[parts.length - 1];
  }

  public void setPrimitiveName(String constName){
    typeSymbol.setName(constName);
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
    return getPrimitiveName();
  }

  @Override
  public String printFullName(){
    return print();
  }

  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    // Care: the following String needs to be adapted if the package was renamed
    jp.member(JsonDeSers.KIND, "de.monticore.types.check.SymTypePrimitive");
    jp.member("primitiveName", getPrimitiveName());
    jp.endObject();
    return jp.getContent();
  }


  /**
   * List of potential constants
   * (on purpose not implemented as enum)
   */
  public static final List<String> primitiveTypes = Collections.unmodifiableList(Arrays
      .asList("boolean", "byte", "char", "short", "int", "long", "float", "double", "void"));

  /**
   * Map for unboxing const types (e.g. "java.lang.Boolean" -> "boolean")
   */
  public static final Map<String, String> unboxMap;


  /**
   * Map for boxing const types (e.g. "boolean" -> "java.lang.Boolean")
   * Results are fully qualified.
   */
  public static final Map<String, String> boxMap;



  /**
   * initializing the maps
   */
  static {
    Map<String, String> unboxMap_temp = new HashMap<String, String>();
    unboxMap_temp.put("java.lang.Boolean", "boolean");
    unboxMap_temp.put("java.lang.Byte", "byte");
    unboxMap_temp.put("java.lang.Character", "char");
    unboxMap_temp.put("java.lang.Short", "short");
    unboxMap_temp.put("java.lang.Integer", "int");
    unboxMap_temp.put("java.lang.Long", "long");
    unboxMap_temp.put("java.lang.Float", "float");
    unboxMap_temp.put("java.lang.Double", "double");
    unboxMap_temp.put("java.lang.String", "String");
    unboxMap_temp.put("Boolean", "boolean");
    unboxMap_temp.put("Byte", "byte");
    unboxMap_temp.put("Character", "char");
    unboxMap_temp.put("Short", "short");
    unboxMap_temp.put("Integer", "int");
    unboxMap_temp.put("Long", "long");
    unboxMap_temp.put("Float", "float");
    unboxMap_temp.put("Double", "double");
    unboxMap = Collections.unmodifiableMap(unboxMap_temp);

    Map<String, String> boxMap_temp = new HashMap<String, String>();
    boxMap_temp.put("boolean", "java.lang.Boolean");
    boxMap_temp.put("byte", "java.lang.Byte");
    boxMap_temp.put("char", "java.lang.Character");
    boxMap_temp.put("double", "java.lang.Double");
    boxMap_temp.put("float", "java.lang.Float");
    boxMap_temp.put("int", "java.lang.Integer");
    boxMap_temp.put("long", "java.lang.Long");
    boxMap_temp.put("short", "java.lang.Short");
    boxMap_temp.put("String", "java.lang.String");
    boxMap = Collections.unmodifiableMap(boxMap_temp);
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
    return "int".equals(getPrimitiveName()) ||
        "byte".equals(getPrimitiveName()) ||
        "short".equals(getPrimitiveName()) ||
        "long".equals(getPrimitiveName()) ||
        "char".equals(getPrimitiveName());
  }

  /**
   * Checks whether it is a numeric type (incl. byte, long, char, float)
   *
   * @return true if the given type is a numeric type
   */
  public boolean isNumericType() {
    return "float".equals(getPrimitiveName()) ||
        "double".equals(getPrimitiveName()) ||
        isIntegralType();
  }

  /**
   * Am I primitive? (such as "int")
   */
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public SymTypePrimitive deepClone() {
    return new SymTypePrimitive(this.typeSymbol);
  }


  @Override
  public boolean deepEquals(SymTypeExpression sym){
    if(!(sym instanceof SymTypePrimitive)){
      return false;
    }
    SymTypePrimitive symPrim = (SymTypePrimitive) sym;
    if(this.typeSymbol == null ||symPrim.typeSymbol ==null){
      return false;
    }
    if(!this.typeSymbol.getName().equals(symPrim.typeSymbol.getName())){
      return false;
    }
    return this.print().equals(symPrim.print());
  }


  // --------------------------------------------------------------------------
}
