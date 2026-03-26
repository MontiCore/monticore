/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypePrimitive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Prints SymTypeExpressions in a Java compatible way,
 */
public class SymTypeExpressionBoxedJavaPrinterVisitor
    extends SymTypeExpressionJavaPrinterVisitor {

  protected Map<String, String> boxMap;

  public SymTypeExpressionBoxedJavaPrinterVisitor() {
    Map<String, String> boxMap_temp = new HashMap<>();
    boxMap_temp.put("boolean", "java.lang.Boolean");
    boxMap_temp.put("byte", "java.lang.Byte");
    boxMap_temp.put("char", "java.lang.Character");
    boxMap_temp.put("double", "java.lang.Double");
    boxMap_temp.put("float", "java.lang.Float");
    boxMap_temp.put("int", "java.lang.Integer");
    boxMap_temp.put("long", "java.lang.Long");
    boxMap_temp.put("short", "java.lang.Short");
    boxMap = Collections.unmodifiableMap(boxMap_temp);
  }

  @Override
  public void visit(SymTypePrimitive symType) {
    getPrint().append(box(symType.getPrimitiveName()));
  }

  @Override
  public void visit(SymTypeOfSIUnit siUnit) {
    getPrint().append(box("double"));
  }

  protected String box(String unboxedName) {
    return boxMap.getOrDefault(unboxedName, unboxedName);
  }
}
