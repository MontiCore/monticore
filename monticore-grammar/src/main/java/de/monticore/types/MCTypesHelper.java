/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.Names;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// TODO: improve implementations
public class MCTypesHelper {

  public static final String OPTIONAL = "Optional";
  
  public static boolean isOptional(ASTMCType type) {
    return isGenericTypeWithOneTypeArgument(type, OPTIONAL);
  }
  
  public static boolean isPrimitive(ASTMCType type) {
    return type instanceof ASTMCPrimitiveType;
  }
  
  public static ASTMCTypeArgument getReferenceTypeFromOptional(ASTMCType type) {
    Preconditions.checkArgument(isOptional(type));
    return ((ASTMCGenericType) type)
        .getMCTypeArgumentList().get(0);
  }
  
  public static ASTMCType getSimpleReferenceTypeFromOptional(ASTMCType type) {
    Preconditions.checkArgument(isOptional(type));
    ASTMCTypeArgument refType = getReferenceTypeFromOptional(type);
    // TODO: improve
    if (refType instanceof ASTMCWildcardTypeArgument
        && ((ASTMCWildcardTypeArgument) refType).isPresentUpperBound()) {
      return((ASTMCWildcardTypeArgument) refType).getUpperBound();
    }
    // TODO: improve
    Preconditions.checkState(refType instanceof ASTMCGenericType);
    return (ASTMCGenericType) refType;
  }
  
  public static String getReferenceNameFromOptional(ASTMCType type) {
    Preconditions.checkArgument(isOptional(type));
    // TODO: improve
    ASTMCTypeArgument reference = ((ASTMCGenericType) type)
        .getMCTypeArgumentList().get(0);
    // TODO MB
//    if (reference instanceof ASTMCWildcardTypeArgument
//        && ((ASTMCWildcardTypeArgument) reference).isPresentUpperBound()) {
//      reference = ((ASTMCWildcardTypeArgument) reference).getUpperBound();
//    }
    Preconditions.checkArgument(reference instanceof ASTMCGenericType);
    List<String> names = ((ASTMCGenericType) reference).getNameList();
    return names.isEmpty() ? "" : names.get(names.size() - 1);
  }
  
  public static String getQualifiedReferenceNameFromOptional(ASTMCType type) {
    Preconditions.checkArgument(isOptional(type));
    // TODO: improve
    ASTMCTypeArgument reference = ((ASTMCGenericType) type)
        .getMCTypeArgumentList().get(0);
    // TODO MB
//    if (reference instanceof ASTMCWildcardTypeArgument
//        && ((ASTMCWildcardTypeArgument) reference).isPresentUpperBound()) {
//      reference = ((ASTMCWildcardTypeArgument) reference).getUpperBound();
//    }
    Preconditions.checkArgument(reference instanceof ASTMCGenericType);
    List<String> names = ((ASTMCGenericType) reference).getNameList();
    return names.isEmpty() ? "" : Names.getQualifiedName(names);
  }

  public static boolean isGenericTypeWithOneTypeArgument(ASTMCType type, String simpleRefTypeName) {
    if (!(type instanceof ASTMCBasicGenericType)) {
      return false;
    }
    ASTMCGenericType simpleRefType = (ASTMCGenericType) type;
    if (simpleRefType.getMCTypeArgumentList().size() != 1) {
      return false;
    }

    if (simpleRefType.getNameList().size() == 1 && simpleRefTypeName.contains(".")) {
      if (simpleRefTypeName.endsWith("." + simpleRefType.getNameList().get(0))){
        return true;
      }
    }
    if (Names.getQualifiedName(simpleRefType.getNameList()).equals(simpleRefTypeName)) {
      return true;
    }
    return false;
  }
  
  public static int getArrayDimensionIfArrayOrZero(ASTMCType astType) {
    return (astType instanceof ASTMCArrayType)? ((ASTMCArrayType) astType).getDimensions() : 0;
  }
  
  public static Optional<ASTMCGenericType> getFirstTypeArgumentOfGenericType(ASTMCType type,
      String simpleRefTypeName) {
    if (!isGenericTypeWithOneTypeArgument(type, simpleRefTypeName)) {
      return Optional.empty();
    }
    ASTMCGenericType simpleRefType = (ASTMCGenericType) type;
    ASTMCTypeArgument typeArgument = simpleRefType
        .getMCTypeArgumentList().get(0);
    if (!(typeArgument instanceof ASTMCGenericType)) {
      return Optional.empty();
    }
    
    return Optional.of((ASTMCGenericType) typeArgument);
  }
  
  /**
   * Gets the first type argument of the generic type
   * 
   * @param type - generic type (the Optional in Optional<ASTNode>)
   * @return -the first type argument (the ASTNode in Optional<ASTNode>)
   */
  public static Optional<ASTMCGenericType> getFirstTypeArgumentOfOptional(
      ASTMCType type) {
    return getFirstTypeArgumentOfGenericType(type, OPTIONAL);
  }
  
  public static String getSimpleName(ASTMCGenericType simpleType) {
    String name = "";
    List<String> qualifiedName = simpleType.getNameList();
    if (qualifiedName != null && !qualifiedName.isEmpty()) {
      name = qualifiedName.get(qualifiedName.size() - 1);
    }
    return name;
  }
  
  public static List<String> createListFromDotSeparatedString(String s) {
    return Arrays.asList(s.split("\\."));
  }
  
  public static String printType(ASTMCType type) {
    if (isOptional(type)) {
      ASTMCTypeArgument ref = getReferenceTypeFromOptional(type);
      return printType(ref);
    }
    return BasicGenericsTypesPrinter.printType(type);
  }
  
  public static boolean isNullable(ASTMCType type) {
    return !isPrimitive(type);
  }
  
  public static String printType(ASTMCTypeArgument type) {
    // TODO MB
//    if (type instanceof ASTMCWildcardTypeArgument) {
//      return BasicGenericsTypesPrinter.printWildcardType((ASTMCWildcardTypeArgument) type);
//    }
    return printType((ASTMCType) type);
  }
  
  public static String printSimpleRefType(ASTMCType type) {
    if (isOptional(type)) {
      return printType(getSimpleReferenceTypeFromOptional(type));
    }
    return BasicGenericsTypesPrinter.printType(type);
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
