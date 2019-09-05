/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types2.SymTypeOfObject;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
import de.monticore.types2.SymTypeExpressionFactory;

import java.util.Arrays;
import java.util.List;

public class TypesCalculatorHelper {
  
  public static boolean isPrimitiveType(SymTypeExpression type) {
    return type.isPrimitiveType();
  }
  
  
  // --------------------------------------------------------------------------
  
  public static SymTypeExpression getUnaryNumericPromotionType(SymTypeExpression type){
    if("byte".equals(unbox(type).getName())||
        "short".equals(unbox(type).getName())||
        "char".equals(unbox(type).getName())||
        "int".equals(unbox(type).getName())
    ){
      type = new SymTypeConstant();
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
  
  // TODO: es existiert Ersatz: SymTypeConstant.unbox
  @Deprecated
  public static SymTypeExpression unbox(SymTypeExpression type){
    if("java.lang.Boolean".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("boolean");
    }else if("java.lang.Byte".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("byte");
    }else if("java.lang.Character".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("char");
    }else if("java.lang.Short".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("short");
    }else if("java.lang.Integer".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("int");
    }else if("java.lang.Long".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("long");
    }else if("java.lang.Float".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("float");
    }else if("java.lang.Double".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("double");
    }else if("java.lang.String".equals(type.getName())){
      type = new SymTypeOfObject();
      type.setName("String");
    }else if("Boolean".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("boolean");
    }else if("Byte".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("byte");
    }else if("Character".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("char");
    }else if("Short".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("short");
    }else if("Integer".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("int");
    }else if("Long".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("long");
    }else if("Float".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("float");
    }else if("Double".equals(type.getName())){
      type = new SymTypeConstant();
      type.setName("double");
    }else if("String".equals(type.getName())){
      type = new SymTypeOfObject();
      type.setName("String");
    }
    return type;
  }
  
  // TODO: es existiert Ersatz: SymTypeConstant.box
  @Deprecated
  public static SymTypeExpression box(SymTypeExpression type) {
    if ("boolean".equals(type.getName())) {
      type = new SymTypeOfObject();
      type.setName("java.lang.Boolean");
    }
    if ("byte".equals(type.getName())) {
      type = new SymTypeOfObject();
      type.setName("java.lang.Byte");
    }
    if ("char".equals(type.getName())) {
      type = new SymTypeOfObject();
      type.setName("java.lang.Character");
    }
    if ("short".equals(type.getName())) {
      type = new SymTypeOfObject();
      type.setName("java.lang.Short");
    }
    if ("int".equals(type.getName())) {
      type = new SymTypeOfObject();
      type.setName("java.lang.Integer");
    }
    if ("long".equals(type.getName())) {
      type = new SymTypeOfObject();
      type.setName("java.lang.Long");
    }
    if ("float".equals(type.getName())) {
      type = new SymTypeOfObject();
      type.setName("java.lang.Float");
    }
    if ("double".equals(type.getName())) {
      type = new SymTypeOfObject();
      type.setName("java.lang.Double");
    }
    return type;
  }

  /**
   * @param type
   * @return true if the given type is an integral type
   */
  // TODO: es existiert Ersatz: SymTypeConstant.isIntegralType
  @Deprecated
  public static boolean isIntegralType(SymTypeExpression type) {
    return type instanceof SymTypeConstant && ((SymTypeConstant)type).isIntegralType();
  }
  
  
  // TODO: es existiert Ersatz: SymTypeConstant.isIntegralType
  @Deprecated
  public static boolean isNumericType(SymTypeExpression type) {
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

  
  
  public static SymTypeExpression mcType2TypeExpression(ASTMCBasicTypesNode type){
    MCTypeVisitor visitor = new MCTypeVisitor();
    type.accept(visitor);
    return visitor.mapping.get(type);
  }

  //TODO check correctnes in all situations, in testHelper wenn nur für Dummy benutzt
  @Deprecated
  public static SymTypeExpression fromETypeSymbol(ETypeSymbol type) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      if (primitiveTypes.contains(type.getName())) {
        return SymTypeExpressionFactory.createTypeConstant(type.getName());
      } else {
        SymTypeOfObject o = new SymTypeOfObject();
        o.setName(type.getName());
        return o;
      }
    }
    return null;
  }
  //TODO check correctness in all situations, in testHelper wenn nur für Dummy benutzt
  @Deprecated
  public static SymTypeExpression fromEVariableSymbol(EVariableSymbol type) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      if (primitiveTypes.contains(type.getName())) {
        return SymTypeExpressionFactory.createTypeConstant(type.getName());
      } else {
        SymTypeOfObject o = new SymTypeOfObject();
        o.setName(type.getName());
        return o;
      }
    }
    return null;
  }
  //TODO check correctnes in all situations, in testHelper wenn nur für Dummy benutzt

  @Deprecated
  public static SymTypeExpression fromEMethodSymbol(EMethodSymbol type) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      if (primitiveTypes.contains(type.getName())) {
        return SymTypeExpressionFactory.createTypeConstant(type.getName());
      } else {
        SymTypeOfObject o = new SymTypeOfObject();
        o.setName(type.getName());
        return o;
      }
    }
    return null;
  }

  //TODO check correctnes in all situations, in testHelper wenn nur für Dummy benutzt
  public static SymTypeExpression fromMethodSymbol(MethodSymbol type) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      if (primitiveTypes.contains(type.getName())) {
        return SymTypeExpressionFactory.createTypeConstant(type.getName());
      } else {
        SymTypeOfObject o = new SymTypeOfObject();
        o.setName(type.getName());
        return o;
      }
    }
    return null;
  }

  public static SymTypeExpression fromFieldSymbol(FieldSymbol type) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      if (primitiveTypes.contains(type.getName())) {
        return SymTypeExpressionFactory.createTypeConstant(type.getName());
      } else {
        SymTypeOfObject o = new SymTypeOfObject();
        o.setName(type.getName());
        return o;
      }
    }
    return null;
  }

  public static SymTypeExpression fromTypeSymbol(TypeSymbol type) {
    List<String> primitiveTypes = Arrays
            .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      if (primitiveTypes.contains(type.getName())) {
        return SymTypeExpressionFactory.createTypeConstant(type.getName());
      } else {
        SymTypeOfObject o = new SymTypeOfObject();
        o.setName(type.getName());
        return o;
      }
    }
    return null;
  }

}
