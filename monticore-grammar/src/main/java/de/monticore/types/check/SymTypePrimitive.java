/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types3.ISymTypeVisitor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class SymTypePrimitive extends SymTypeExpression {

  protected TypeSymbol typeSymbol;

  public SymTypePrimitive(TypeSymbol typeSymbol) {
    this.typeSymbol = typeSymbol;
  }

  @Override
  public boolean hasTypeInfo() {
    return typeSymbol != null;
  }

  @Override
  public TypeSymbol getTypeInfo() {
    return typeSymbol;
  }

  public String getPrimitiveName() {
    return typeSymbol.getName();
  }

  public String getBoxedPrimitiveName() {
    return box(typeSymbol.getName());
  }

  /**
   * @deprecated only used in 1 test ONCE... in our main projects
   */
  @Deprecated
  public String getBaseOfBoxedName() {
    String[] parts = box(typeSymbol.getName()).split("\\.");
    return parts[parts.length - 1];
  }

  /**
   * @deprecated only used in tests in our main projects
   */
  @Deprecated
  public void setPrimitiveName(String constName){
    typeSymbol.setName(constName);
  }

  @Override
  public String print() {
    return getPrimitiveName();
  }

  @Override
  public String printFullName(){
    return print();
  }

  /**
   * List of potential constants
   * (on purpose not implemented as enum)
   * @deprecated cannot assume fixed set for all languages
   */
  @Deprecated
  public static final List<String> primitiveTypes =
      Collections.unmodifiableList(Arrays.asList(
          BasicSymbolsMill.BOOLEAN,
          BasicSymbolsMill.BYTE,
          BasicSymbolsMill.CHAR,
          BasicSymbolsMill.SHORT,
          BasicSymbolsMill.INT,
          BasicSymbolsMill.LONG,
          BasicSymbolsMill.FLOAT,
          BasicSymbolsMill.DOUBLE,
          //deprecated: use SymTypeOfVoid
          BasicSymbolsMill.VOID
      ));

  /**
   * Map for unboxing const types (e.g. "java.lang.Boolean" -> "boolean")
   */
  @Deprecated
  public static final Map<String, String> unboxMap;

  /**
   * Map for boxing const types (e.g. "boolean" -> "java.lang.Boolean")
   * Results are fully qualified.
   */
  @Deprecated
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
    //deprecated: String is not expected
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
    //deprecated: String is not expected
    boxMap_temp.put("String", "java.lang.String");
    boxMap = Collections.unmodifiableMap(boxMap_temp);
  }

  /**
   * unboxing const types (e.g. "java.lang.Boolean" -> "boolean").
   * otherwise return is unchanged
   * @deprecated use SymTypeUnboxingVisitor
   * @param boxedName
   * @return
   */
  @Deprecated
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
   * @deprecated use SymTypeBoxingVisitor
   * @param unboxedName
   * @return
   */
  @Deprecated
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
    return BasicSymbolsMill.INT.equals(getPrimitiveName()) ||
        BasicSymbolsMill.BYTE.equals(getPrimitiveName()) ||
        BasicSymbolsMill.SHORT.equals(getPrimitiveName()) ||
        BasicSymbolsMill.LONG.equals(getPrimitiveName()) ||
        BasicSymbolsMill.CHAR.equals(getPrimitiveName());
  }

  /**
   * Checks whether it is a numeric type (incl. byte, long, char, float)
   *
   * @return true if the given type is a numeric type
   */
  public boolean isNumericType() {
    return BasicSymbolsMill.FLOAT.equals(getPrimitiveName()) ||
        BasicSymbolsMill.DOUBLE.equals(getPrimitiveName()) ||
        isIntegralType();
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public SymTypePrimitive asPrimitive() {
    return this;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    if(!sym.isPrimitive()){
      return false;
    }
    SymTypePrimitive symPrim = (SymTypePrimitive) sym;
    if(!this.typeSymbol.getName().equals(symPrim.typeSymbol.getName())){
      return false;
    }
    return this.print().equals(symPrim.print());
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }

}
