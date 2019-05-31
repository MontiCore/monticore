package de.monticore.typescalculator;

import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypesCalculatorHelper {
  
  public static ASTMCType getUnaryNumericPromotionType(ASTMCType type){
    if("byte".equals(unbox(type).getBaseName())||
        "short".equals(unbox(type).getBaseName())||
        "char".equals(unbox(type).getBaseName())||
        "int".equals(unbox(type).getBaseName())
    ){
      return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
    }
    if("long".equals(unbox(type).getBaseName())||
        "double".equals(unbox(type).getBaseName())||
        "float".equals(unbox(type).getBaseName())
    ){
      return unbox(type);
    }
    return type;
  }
  
  public static ASTMCType unbox(ASTMCType type){
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("Boolean");
    if (type.getBaseName().equals("Boolean") || MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(type)) {
      return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
    }
    name.remove("Boolean");
    name.add("Byte");
    if (type.getBaseName().equals("Byte") || MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(type)) {
      return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BYTE).build();
    }
    name.remove("Byte");
    name.add("Character");
    if (type.getBaseName().equals("Character") || MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(type)) {
      return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build();
    }
    name.remove("Character");
    name.add("Short");
    if (type.getBaseName().equals("Short") || MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(type)) {
      return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.SHORT).build();
    }
    name.remove("Short");
    name.add("Integer");
    if (type.getBaseName().equals("Integer") || MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(type)) {
      return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
    }
    name.remove("Integer");
    name.add("Long");
    if (type.getBaseName().equals("Long") || MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(type)) {
      return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build();
    }
    name.remove("Long");
    name.add("Float");
    if (type.getBaseName().equals("Float") || MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(type)) {
      return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build();
    }
    name.remove("Float");
    name.add("Double");
    if (type.getBaseName().equals("Double") || MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build().deepEquals(type)) {
      return MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
    }
    name.remove("Double");

    return type;
  }

  public static ASTMCType box(ASTMCType type) {
    List<String> name = new ArrayList<>();
    name.add("java");
    name.add("lang");
    name.add("Boolean");
    if (type.getBaseName().equals("boolean")) {
      MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    }
    name.remove("Boolean");
    name.add("Byte");
    if (type.getBaseName().equals("byte")) {
      MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    }
    name.remove("Byte");
    name.add("Character");
    if (type.getBaseName().equals("char")) {
      MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    }
    name.remove("Character");
    name.add("Short");
    if (type.getBaseName().equals("short")) {
      MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    }
    name.remove("Short");
    name.add("Integer");
    if (type.getBaseName().equals("int")) {
      MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    }
    name.remove("Integer");
    name.add("Long");
    if (type.getBaseName().equals("long")) {
      MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    }
    name.remove("Long");
    name.add("Float");
    if (type.getBaseName().equals("float")) {
      MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    }
    name.remove("Float");
    name.add("Double");
    if (type.getBaseName().equals("double")) {
      MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
    }

    return type;
  }

  /**
   * @param type
   * @return true if the given type is an integral type
   */
  public static boolean isIntegralType(ASTMCType type) {
    if ("int".equals(unbox(type).getBaseName()))
      return true;
    if ("byte".equals(unbox(type).getBaseName()))
      return true;
    if ("short".equals(unbox(type).getBaseName()))
      return true;
    if ("long".equals(unbox(type).getBaseName()))
      return true;
    if ("char".equals(unbox(type).getBaseName()))
      return true;
    return false;
  }

  public static boolean isNumericType(ASTMCType type) {
    if (isIntegralType(type))
      return true;
    if ("float".equals(unbox(type).getBaseName()))
      return true;
    if ("double".equals(unbox(type).getBaseName()))
      return true;
    return false;
  }

  public static boolean isPrimitiveType(ASTMCType type) {
    List<String> primitiveTypes = Arrays
        .asList("boolean", "byte", "char", "short", "int", "long", "float", "double");
    if (type != null ) {
      return primitiveTypes.contains(unbox(type).getBaseName());
    }
    return false;
  }
}
