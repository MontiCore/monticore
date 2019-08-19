/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

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
      type = new TypeExpression();
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
    if(type.getName().equals("java.lang.Boolean")){
      type = new TypeExpression();
      type.setName("boolean");
    }else if(type.getName().equals("java.lang.Byte")){
      type = new TypeExpression();
      type.setName("byte");
    }else if(type.getName().equals("java.lang.Character")){
      type = new TypeExpression();
      type.setName("char");
    }else if(type.getName().equals("java.lang.Short")){
      type = new TypeExpression();
      type.setName("short");
    }else if(type.getName().equals("java.lang.Integer")){
      type = new TypeExpression();
      type.setName("int");
    }else if(type.getName().equals("java.lang.Long")){
      type = new TypeExpression();
      type.setName("long");
    }else if(type.getName().equals("java.lang.Float")){
      type = new TypeExpression();
      type.setName("float");
    }else if(type.getName().equals("java.lang.Double")){
      type = new TypeExpression();
      type.setName("double");
    }else if(type.getName().equals("java.lang.String")){
      type = new TypeExpression();
      type.setName("String");
    }else if(type.getBaseName().equals("Boolean")){
      type = new TypeExpression();
      type.setName("boolean");
    }else if(type.getBaseName().equals("Byte")){
      type = new TypeExpression();
      type.setName("byte");
    }else if(type.getBaseName().equals("Character")){
      type = new TypeExpression();
      type.setName("char");
    }else if(type.getBaseName().equals("Short")){
      type = new TypeExpression();
      type.setName("short");
    }else if(type.getBaseName().equals("Integer")){
      type = new TypeExpression();
      type.setName("int");
    }else if(type.getBaseName().equals("Long")){
      type = new TypeExpression();
      type.setName("long");
    }else if(type.getBaseName().equals("Float")){
      type = new TypeExpression();
      type.setName("float");
    }else if(type.getBaseName().equals("Double")) {
      type = new TypeExpression();
      type.setName("double");
    }else if(type.getBaseName().equals("String")){
      type = new TypeExpression();
      type.setName("String");
    }
    return type;
  }

  public static TypeExpression box(TypeExpression type) {
    if (type.getName().equals("boolean")) {
      type = new TypeExpression();
      type.setName("java.lang.Boolean");
    }
    if (type.getName().equals("byte")) {
      type = new TypeExpression();
      type.setName("java.lang.Byte");
    }
    if (type.getName().equals("char")) {
      type = new TypeExpression();
      type.setName("java.lang.Character");
    }
    if (type.getName().equals("short")) {
      type = new TypeExpression();
      type.setName("java.lang.Short");
    }
    if (type.getName().equals("int")) {
      type = new TypeExpression();
      type.setName("java.lang.Integer");
    }
    if (type.getName().equals("long")) {
      type = new TypeExpression();
      type.setName("java.lang.Long");
    }
    if (type.getName().equals("float")) {
      type = new TypeExpression();
      type.setName("java.lang.Float");
    }
    if (type.getName().equals("double")) {
      type = new TypeExpression();
      type.setName("java.lang.Double");
    }
    return type;
  }

  /**
   * @param type
   * @return true if the given type is an integral type
   */
  public static boolean isIntegralType(TypeExpression type) {
    if ("int".equals(unbox(type).getName()))
      return true;
    if ("byte".equals(unbox(type).getName()))
      return true;
    if ("short".equals(unbox(type).getName()))
      return true;
    if ("long".equals(unbox(type).getName()))
      return true;
    if ("char".equals(unbox(type).getName()))
      return true;
    return false;
  }

  public static boolean isNumericType(TypeExpression type) {
    if (isIntegralType(type))
      return true;
    if ("float".equals(unbox(type).getName()))
      return true;
    if ("double".equals(unbox(type).getName()))
      return true;
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
}
