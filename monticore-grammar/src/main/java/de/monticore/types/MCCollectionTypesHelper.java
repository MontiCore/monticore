/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.se_rwth.commons.Names;

import java.util.Optional;

public class MCCollectionTypesHelper extends MCBasicTypesHelper {


  public static ASTMCTypeArgument getReferenceTypeFromOptional(ASTMCType type) {
    Preconditions.checkArgument(isOptional(type));
    return ((ASTMCGenericType) type).getMCTypeArgumentList().get(0);
  }

  public static Optional<ASTMCTypeArgument> getFirstTypeArgumentOfGenericType(ASTMCType type,
                                                                              String simpleRefTypeName) {
    if (!isGenericTypeWithOneTypeArgument(type, simpleRefTypeName)) {
      return Optional.empty();
    }
    ASTMCGenericType simpleRefType = (ASTMCGenericType) type;
    ASTMCTypeArgument typeArgument = simpleRefType.getMCTypeArgumentList().get(0);

    return Optional.of(typeArgument);
  }

  public static final String OPTIONAL = "Optional";

  /**
   * Gets the first type argument of the generic type
   *
   * @param type - generic type (the Optional in Optional<ASTNode>)
   * @return -the first type argument (the ASTNode in Optional<ASTNode>)
   */
  public static Optional<ASTMCTypeArgument> getFirstTypeArgumentOfOptional(
      ASTMCType type) {
    return getFirstTypeArgumentOfGenericType(type, OPTIONAL);
  }

  public static String getSimpleName(ASTMCGenericType simpleType) {
    return Names.getSimpleName(simpleType.printWithoutTypeArguments());
  }

  public static String printSimpleRefType(ASTMCType type) {
    return SimpleGenericTypesPrinter.printType(type);
  }


  public static String printType(ASTMCType type) {
    return SimpleGenericTypesPrinter.printType(type);
  }

  public static String printType(ASTMCTypeArgument type) {
    return SimpleGenericTypesPrinter.printType(type);
  }

  public static boolean isOptional(ASTMCType type) {
    return isGenericTypeWithOneTypeArgument(type, OPTIONAL);
  }

  public static boolean isGenericTypeWithOneTypeArgument(ASTMCType type, String simpleRefTypeName) {
    if (!(type instanceof ASTMCGenericType)) {
      return false;
    }
    ASTMCGenericType simpleRefType = (ASTMCGenericType) type;
    if (simpleRefType.getMCTypeArgumentList().isEmpty() || simpleRefType.getMCTypeArgumentList().size() != 1) {
      return false;
    }

    if (simpleRefType.printType().split("\\.").length == 1 && simpleRefTypeName.contains(".")) {
      if (simpleRefTypeName.endsWith("." + simpleRefType.printWithoutTypeArguments())) {
        return true;
      }
    }
    if (simpleRefType.printWithoutTypeArguments().equals(simpleRefTypeName)) {
      return true;
    }
    return false;
  }
}
