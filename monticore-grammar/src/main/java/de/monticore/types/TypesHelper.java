/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.monticore.types.types._ast.ASTArrayType;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTWildcardType;
import de.se_rwth.commons.Names;

// TODO: improve implementations
public class TypesHelper {
  
  public static final String OPTIONAL = "Optional";
  
  public static boolean isOptional(ASTType type) {
    return isGenericTypeWithOneTypeArgument(type, OPTIONAL);
  }
  
  public static boolean isPrimitive(ASTType type) {
    return type instanceof ASTPrimitiveType;
  }
  
  public static ASTTypeArgument getReferenceTypeFromOptional(ASTType type) {
    Preconditions.checkArgument(isOptional(type));
    return ((ASTSimpleReferenceType) type).getTypeArguments()
        .getTypeArgumentList().get(0);
  }
  
  public static ASTSimpleReferenceType getSimpleReferenceTypeFromOptional(ASTType type) {
    Preconditions.checkArgument(isOptional(type));
    ASTTypeArgument refType = getReferenceTypeFromOptional(type);
    // TODO: improve
    if (refType instanceof ASTWildcardType
        && ((ASTWildcardType) refType).isPresentUpperBound()) {
      refType = ((ASTWildcardType) refType).getUpperBound();
    }
    // TODO: improve
    Preconditions.checkState(refType instanceof ASTSimpleReferenceType);
    return (ASTSimpleReferenceType) refType;
  }
  
  public static String getReferenceNameFromOptional(ASTType type) {
    Preconditions.checkArgument(isOptional(type));
    // TODO: improve
    ASTTypeArgument reference = ((ASTSimpleReferenceType) type).getTypeArguments()
        .getTypeArgumentList().get(0);
    if (reference instanceof ASTWildcardType
        && ((ASTWildcardType) reference).isPresentUpperBound()) {
      reference = ((ASTWildcardType) reference).getUpperBound();
    }
    Preconditions.checkArgument(reference instanceof ASTSimpleReferenceType);
    List<String> names = ((ASTSimpleReferenceType) reference).getNameList();
    return names.isEmpty() ? "" : names.get(names.size() - 1);
  }
  
  public static String getQualifiedReferenceNameFromOptional(ASTType type) {
    Preconditions.checkArgument(isOptional(type));
    // TODO: improve
    ASTTypeArgument reference = ((ASTSimpleReferenceType) type).getTypeArguments()
        .getTypeArgumentList().get(0);
    if (reference instanceof ASTWildcardType
        && ((ASTWildcardType) reference).isPresentUpperBound()) {
      reference = ((ASTWildcardType) reference).getUpperBound();
    }
    Preconditions.checkArgument(reference instanceof ASTSimpleReferenceType);
    List<String> names = ((ASTSimpleReferenceType) reference).getNameList();
    return names.isEmpty() ? "" : Names.getQualifiedName(names);
  }
  
  public static boolean isGenericTypeWithOneTypeArgument(ASTType type, String simpleRefTypeName) {
    if (!(type instanceof ASTSimpleReferenceType)) {
      return false;
    }
    ASTSimpleReferenceType simpleRefType = (ASTSimpleReferenceType) type;
    if (!Names.getQualifiedName(simpleRefType.getNameList()).equals(
        simpleRefTypeName)
        ||
        !simpleRefType.isPresentTypeArguments() ||
        simpleRefType.getTypeArguments().getTypeArgumentList().size() != 1) {
      return false;
    }
    return true;
  }
  
  public static int getArrayDimensionIfArrayOrZero(ASTType astType) {
    return (astType instanceof ASTArrayType)? ((ASTArrayType) astType).getDimensions() : 0;
  }
  
  public static Optional<ASTSimpleReferenceType> getFirstTypeArgumentOfGenericType(ASTType type,
      String simpleRefTypeName) {
    if (!isGenericTypeWithOneTypeArgument(type, simpleRefTypeName)) {
      return Optional.empty();
    }
    ASTSimpleReferenceType simpleRefType = (ASTSimpleReferenceType) type;
    ASTTypeArgument typeArgument = simpleRefType
        .getTypeArguments().getTypeArgumentList().get(0);
    if (!(typeArgument instanceof ASTSimpleReferenceType)) {
      return Optional.empty();
    }
    
    return Optional.of((ASTSimpleReferenceType) typeArgument);
  }
  
  /**
   * Gets the first type argument of the generic type
   * 
   * @param type - generic type (the Optional in Optional<ASTNode>)
   * @return -the first type argument (the ASTNode in Optional<ASTNode>)
   */
  public static Optional<ASTSimpleReferenceType> getFirstTypeArgumentOfOptional(
      ASTType type) {
    return getFirstTypeArgumentOfGenericType(type, OPTIONAL);
  }
  
  public static String getSimpleName(ASTSimpleReferenceType simpleType) {
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
  
  public static String printType(ASTType type) {
    if (isOptional(type)) {
      ASTTypeArgument ref = getReferenceTypeFromOptional(type);
      return printType(ref);
    }
    return TypesPrinter.printType(type);
  }
  
  public static boolean isNullable(ASTType type) {
    return !isPrimitive(type);
  }
  
  public static String printType(ASTTypeArgument type) {
    if (type instanceof ASTWildcardType) {
      return TypesPrinter.printWildcardType((ASTWildcardType) type);
    }
    return printType((ASTType) type);
  }
  
  public static String printSimpleRefType(ASTType type) {
    if (isOptional(type)) {
      return printType(getSimpleReferenceTypeFromOptional(type));
    }
    return TypesPrinter.printType(type);
  }
  
  public static int getPrimitiveType(String typeName) {
    if (Strings.isNullOrEmpty(typeName)) {
      return -1;
    }
    switch (typeName) {
      case "boolean":
        return ASTConstantsTypes.BOOLEAN;
      case "float":
        return ASTConstantsTypes.FLOAT;
      case "byte":
        return ASTConstantsTypes.BYTE;
      case "char":
        return ASTConstantsTypes.CHAR;
      case "double":
        return ASTConstantsTypes.DOUBLE;
      case "int":
        return ASTConstantsTypes.INT;
      case "short":
        return ASTConstantsTypes.SHORT;
      case "long":
        return ASTConstantsTypes.LONG;
      default:
        return -1;
    }
  }
  
}
