/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import com.google.common.base.Strings;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Arrays;
import java.util.List;

public class MCBasicTypesHelper {
  
  /**
   * Exactly the ASTMCPrimitiveType is primitive; all others are not.
   * Please aslo note that Type variables are not regarded as
   * primitives, because they ma be not primitive
   * @param type
   * @return
   */
  public static boolean isPrimitive(ASTMCType type) {
    return type instanceof ASTMCPrimitiveType;
  }
  
  /**
   * Separate full qualified String into list of Strings (using "." as separator)
   * @param s
   * @return
   */
  public static List<String> createListFromDotSeparatedString(String s) {
    return Arrays.asList(s.split("\\."));
  }
  
  /**
   * Nullable are in principle all types, except the primitives.
   * TODO: Erroneous implementation:
   * However that is wrong in general, type variable e.g. are not necessarily nullable
   * because they might stand for a primitive type.
   * @param type
   * @return
   */
  @Deprecated
  public static boolean isNullable(ASTMCType type) {
    return !isPrimitive(type);
  }
  
  /**
   * Map the String with a primitive type, e.g. "int" to its
   * enumerative Number, e.g.   ASTConstantsMCBasicTypes.INT
   * @param typeName
   * @return
   */
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