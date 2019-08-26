package de.monticore.typescalculator;

import java.util.Arrays;
import java.util.List;

public class TypeExpressionBuilder {
  public static TypeExpression buildTypeConstant(String name) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (primitiveTypes.contains(name) ) {
      TypeConstant o = new TypeConstant();
      o.setName(name);
      return o;
    }
    return null;
  }
}
