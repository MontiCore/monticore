/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;

import java.util.Arrays;
import java.util.List;

public class TypesCalculatorHelper {

  public static TypeExpression getUnaryNumericPromotionType(TypeExpression type){
    if("byte".equals(unbox(type).getName())||
        "short".equals(unbox(type).getName())||
        "char".equals(unbox(type).getName())||
        "int".equals(unbox(type).getName())
    ){
      type = new TypeConstant();
      type.setName("int");
      return type;
    }
    if("long".equals(unbox(type).getName())||
        "double".equals(unbox(type).getName())||
        "float".equals(unbox(type).getName())
    ){
      return unbox(type);
    }
    return type;
  }

  public static TypeExpression unbox(TypeExpression type){
    if("java.lang.Boolean".equals(type.getName())){
      type = new TypeConstant();
      type.setName("boolean");
    }else if("java.lang.Byte".equals(type.getName())){
      type = new TypeConstant();
      type.setName("byte");
    }else if("java.lang.Character".equals(type.getName())){
      type = new TypeConstant();
      type.setName("char");
    }else if("java.lang.Short".equals(type.getName())){
      type = new TypeConstant();
      type.setName("short");
    }else if("java.lang.Integer".equals(type.getName())){
      type = new TypeConstant();
      type.setName("int");
    }else if("java.lang.Long".equals(type.getName())){
      type = new TypeConstant();
      type.setName("long");
    }else if("java.lang.Float".equals(type.getName())){
      type = new TypeConstant();
      type.setName("float");
    }else if("java.lang.Double".equals(type.getName())){
      type = new TypeConstant();
      type.setName("double");
    }else if("java.lang.String".equals(type.getName())){
      type = new ObjectType();
      type.setName("String");
    }else if("Boolean".equals(type.getName())){
      type = new TypeConstant();
      type.setName("boolean");
    }else if("Byte".equals(type.getName())){
      type = new TypeConstant();
      type.setName("byte");
    }else if("Character".equals(type.getName())){
      type = new TypeConstant();
      type.setName("char");
    }else if("Short".equals(type.getName())){
      type = new TypeConstant();
      type.setName("short");
    }else if("Integer".equals(type.getName())){
      type = new TypeConstant();
      type.setName("int");
    }else if("Long".equals(type.getName())){
      type = new TypeConstant();
      type.setName("long");
    }else if("Float".equals(type.getName())){
      type = new TypeConstant();
      type.setName("float");
    }else if("Double".equals(type.getName())){
      type = new TypeConstant();
      type.setName("double");
    }else if("String".equals(type.getName())){
      type = new ObjectType();
      type.setName("String");
    }
    return type;
  }

  public static TypeExpression box(TypeExpression type) {
    if ("boolean".equals(type.getName())) {
      type = new ObjectType();
      type.setName("java.lang.Boolean");
    }
    if ("byte".equals(type.getName())) {
      type = new ObjectType();
      type.setName("java.lang.Byte");
    }
    if ("char".equals(type.getName())) {
      type = new ObjectType();
      type.setName("java.lang.Character");
    }
    if ("short".equals(type.getName())) {
      type = new ObjectType();
      type.setName("java.lang.Short");
    }
    if ("int".equals(type.getName())) {
      type = new ObjectType();
      type.setName("java.lang.Integer");
    }
    if ("long".equals(type.getName())) {
      type = new ObjectType();
      type.setName("java.lang.Long");
    }
    if ("float".equals(type.getName())) {
      type = new ObjectType();
      type.setName("java.lang.Float");
    }
    if ("double".equals(type.getName())) {
      type = new ObjectType();
      type.setName("java.lang.Double");
    }
    return type;
  }

  /**
   * @param type
   * @return true if the given type is an integral type
   */
  public static boolean isIntegralType(TypeExpression type) {
    if ("int".equals(unbox(type).getName())) {
      return true;
    }
    if ("byte".equals(unbox(type).getName())) {
      return true;
    }
    if ("short".equals(unbox(type).getName())) {
      return true;
    }
    if ("long".equals(unbox(type).getName())) {
      return true;
    }
    if ("char".equals(unbox(type).getName())) {
      return true;
    }
    return false;
  }

  public static boolean isNumericType(TypeExpression type) {
    if (isIntegralType(type)) {
      return true;
    }
    if ("float".equals(unbox(type).getName())) {
      return true;
    }
    if ("double".equals(unbox(type).getName())) {
      return true;
    }
    return false;
  }

  public static boolean isPrimitiveType(TypeExpression type) {
    List<String> primitiveTypes = Arrays
        .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      return primitiveTypes.contains(unbox(type).getName());
    }
    return false;
  }


  public static TypeExpression mcType2TypeExpression(ASTMCBasicTypesNode type){
    MCTypeVisitor visitor = new MCTypeVisitor();
    type.accept(visitor);
    return visitor.mapping.get(type);
  }

  //TODO check correctnes in all situations, in testHelper wenn nur für Dummy benutzt
  public static TypeExpression fromETypeSymbol(ETypeSymbol type) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      if (primitiveTypes.contains(type.getName())) {
        return TypeExpressionBuilder.buildTypeConstant(type.getName());
      } else {
        ObjectType o = new ObjectType();
        o.setName(type.getName());
        return o;
      }
    }
    return null;
  }
  //TODO check correctnes in all situations, in testHelper wenn nur für Dummy benutzt
  public static TypeExpression fromEVariableSymbol(EVariableSymbol type) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      if (primitiveTypes.contains(type.getName())) {
        return TypeExpressionBuilder.buildTypeConstant(type.getName());
      } else {
        ObjectType o = new ObjectType();
        o.setName(type.getName());
        return o;
      }
    }
    return null;
  }
  //TODO check correctnes in all situations, in testHelper wenn nur für Dummy benutzt
  public static TypeExpression fromEMethodSymbol(EMethodSymbol type) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      if (primitiveTypes.contains(type.getName())) {
        return TypeExpressionBuilder.buildTypeConstant(type.getName());
      } else {
        ObjectType o = new ObjectType();
        o.setName(type.getName());
        return o;
      }
    }
    return null;
  }
}
