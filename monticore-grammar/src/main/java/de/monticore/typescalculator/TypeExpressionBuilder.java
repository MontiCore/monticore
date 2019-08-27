/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import java.util.Arrays;
import java.util.List;

public class TypeExpressionBuilder {
  public static TypeConstant buildTypeConstant(String name) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (primitiveTypes.contains(name) ) {
      TypeConstant o = new TypeConstant();
      o.setName(name);
      return o;
    }
    return null;
  }

  public static ObjectType buildObjectType(String name, List<TypeExpression> superTypes) {
    ObjectType o = new ObjectType();
    o.setName(name);
    o.setSuperTypes(superTypes);
    return o;
  }

  public static GenericTypeExpression buildGenericTypeExpression(String name, List<TypeExpression> superTypes, List<TypeExpression> arguments){
    GenericTypeExpression o = new GenericTypeExpression();
    o.setName(name);
    o.setArguments(arguments);
    o.setSuperTypes(superTypes);
    return o;
  }

  public static TypeVariable buildTypeVariable(String name, List<TypeExpression> superTypes) {
    TypeVariable o = new TypeVariable();
    o.setName(name);
    o.setSuperTypes(superTypes);
    return o;
  }

}
