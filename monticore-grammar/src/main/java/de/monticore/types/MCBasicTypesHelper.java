/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import com.google.common.base.Strings;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Arrays;
import java.util.List;

public class MCBasicTypesHelper {



  public static boolean isPrimitive(ASTMCType type) {
    return type instanceof ASTMCPrimitiveType;
  }

  public static List<String> createListFromDotSeparatedString(String s) {
    return Arrays.asList(s.split("\\."));
  }

  public static boolean isNullable(ASTMCType type) {
    return !isPrimitive(type);
  }

  public static int getPrimitiveType(String typeName) {
    if (Strings.isNullOrEmpty(typeName)) {
      return -1;
    }
    switch (typeName) {
    case "boolean":
      return ASTConstantsMCBasicTypes.BOOLEAN;
    case "float":
      return ASTConstantsMCBasicTypes.FLOAT;
    case "byte":
      return ASTConstantsMCBasicTypes.BYTE;
    case "char":
      return ASTConstantsMCBasicTypes.CHAR;
    case "double":
      return ASTConstantsMCBasicTypes.DOUBLE;
    case "int":
      return ASTConstantsMCBasicTypes.INT;
    case "short":
      return ASTConstantsMCBasicTypes.SHORT;
    case "long":
      return ASTConstantsMCBasicTypes.LONG;
    default:
      return -1;
    }
  }

}