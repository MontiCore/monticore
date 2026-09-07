/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;

public class MCBasicTypesHelper {
  
  /**
   * Map the String with a primitive type, e.g. "int" to its
   * enumerative Number, e.g.   ASTConstantsMCBasicTypes.INT
   * Returns -1 if illegal name; no error message
   * @param typeName
   * @return
   */
  public static int primitiveName2Const(String typeName) {
    if (null == typeName || typeName.isEmpty()) {
      return -1;
    }
    return switch (typeName) {
      case "boolean" -> ASTConstantsMCBasicTypes.BOOLEAN;
      case "float" -> ASTConstantsMCBasicTypes.FLOAT;
      case "byte" -> ASTConstantsMCBasicTypes.BYTE;
      case "char" -> ASTConstantsMCBasicTypes.CHAR;
      case "double" -> ASTConstantsMCBasicTypes.DOUBLE;
      case "int" -> ASTConstantsMCBasicTypes.INT;
      case "short" -> ASTConstantsMCBasicTypes.SHORT;
      case "long" -> ASTConstantsMCBasicTypes.LONG;
      default -> -1;
    };
  }
  
  /**
   * Map the integer e.g.   ASTConstantsMCBasicTypes.INT
   * to the  respective String with a primitive type, e.g. "int"
   * Returns "unknownType" if illegal number; no error message
   * @param typeConstant
   * @return
   */
  public static String primitiveConst2Name(int typeConstant) {
    return switch (typeConstant) {
      case ASTConstantsMCBasicTypes.BOOLEAN -> "boolean";
      case ASTConstantsMCBasicTypes.BYTE -> "byte";
      case ASTConstantsMCBasicTypes.CHAR -> "char";
      case ASTConstantsMCBasicTypes.SHORT -> "short";
      case ASTConstantsMCBasicTypes.INT -> "int";
      case ASTConstantsMCBasicTypes.FLOAT -> "float";
      case ASTConstantsMCBasicTypes.LONG -> "long";
      case ASTConstantsMCBasicTypes.DOUBLE -> "double";
      default -> "unknownType";
    };
  }
  
}
