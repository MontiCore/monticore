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

  public static TypeExpression buildObjectType(String name, List<TypeExpression> superTypes) {
    ObjectType o = new ObjectType();
    o.setName(name);
    o.setSuperTypes(superTypes);
    return o;
  }

  public static TypeExpression buildGenericTypeExpression(String name, List<TypeExpression> superTypes, List<TypeExpression> arguments){
    GenericTypeExpression o = new GenericTypeExpression();
    o.setName(name);
    o.setArguments(arguments);
    o.setSuperTypes(superTypes);
    return o;
  }
}
