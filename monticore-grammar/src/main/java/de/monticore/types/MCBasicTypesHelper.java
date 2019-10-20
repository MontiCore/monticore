/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import com.google.common.base.Strings;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Arrays;
import java.util.List;

public class MCBasicTypesHelper {
  
  // TODO: Klase kann gelöscht werden, weil sie keine benutzten Funktionen beinhaltet
  
  /**
   * Exactly the ASTMCPrimitiveType is primitive; all others are not.
   * Please aslo note that Type variables are not regarded as
   * primitives, because they ma be not primitive
   * @param type
   * @return
   */
  @Deprecated // because unused
  public static boolean isPrimitive(ASTMCType type) {
    return type instanceof ASTMCPrimitiveType;
  }
  
  /**
   * Separate full qualified String into list of Strings (using "." as separator)
   * @param s
   * @return
   */
  @Deprecated // because unused
  public static List<String> createListFromDotSeparatedString(String s) {
    return Arrays.asList(s.split("\\."));
  }
  
  
  /**
   * Map the String with a primitive type, e.g. "int" to its
   * enumerative Number, e.g.   ASTConstantsMCBasicTypes.INT
   * Returns -1 if illegal name; no error message
   * @param typeName
   * @return
   */
  public static int primitiveName2Const(String typeName) {
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
  
  /**
   * Map the integer e.g.   ASTConstantsMCBasicTypes.INT
   * to the  respective String with a primitive type, e.g. "int"
   * Returns "unknownType" if illegal number; no error message
   * @param typeConstant
   * @return
   */
  public static String primitiveConst2Name(int typeConstant) {
    switch (typeConstant) {
      case ASTConstantsMCBasicTypes.BOOLEAN:
        return "boolean";
      case ASTConstantsMCBasicTypes.BYTE:
        return "byte";
      case ASTConstantsMCBasicTypes.CHAR:
        return "char";
      case ASTConstantsMCBasicTypes.SHORT:
        return "short";
      case ASTConstantsMCBasicTypes.INT:
        return "int";
      case ASTConstantsMCBasicTypes.FLOAT:
        return "float";
      case ASTConstantsMCBasicTypes.LONG:
        return "long";
      case ASTConstantsMCBasicTypes.DOUBLE:
        return "double";
      default:
        return "unknownType";
    }
  }
  
}