/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SynthesizeSymTypeFromMCSimpleGenericTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
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
            return ((ASTMCWildcardTypeArgument) refType).getUpperBound();
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
        return getSimpleName(((ASTMCGenericType) reference));
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
        return getSimpleName((ASTMCGenericType) reference);
    }

  public static boolean isGenericTypeWithOneTypeArgument(ASTMCType type, String simpleRefTypeName) {
    if (!(type instanceof ASTMCBasicGenericType)) {
      return false;
    }
    ASTMCGenericType genericType = (ASTMCGenericType) type;
    if (genericType.getMCTypeArgumentList().size() != 1) {
      return false;
    }

    if (genericType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.").length == 1 && simpleRefTypeName.contains(".")) {
      if (simpleRefTypeName.endsWith("." + genericType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.")[0])) {
        return true;
      }
    }
    if (genericType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).equals(simpleRefTypeName)) {
      return true;
    }
    return false;
  }

    public static int getArrayDimensionIfArrayOrZero(ASTMCType astType) {
        return (astType instanceof ASTMCArrayType) ? ((ASTMCArrayType) astType).getDimensions() : 0;
    }

  public static Optional<ASTMCGenericType> getFirstTypeArgumentOfGenericType(ASTMCType type,
                                                                             String simpleRefTypeName) {
    if (!isGenericTypeWithOneTypeArgument(type, simpleRefTypeName)) {
      return Optional.empty();
    }
    ASTMCGenericType genericType = (ASTMCGenericType) type;
    ASTMCTypeArgument typeArgument = genericType
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
    return Names.getSimpleName(simpleType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()));
  }

    public static List<String> createListFromDotSeparatedString(String s) {
        return Arrays.asList(s.split("\\."));
    }

    public static String printType(ASTMCType type) {
        if (isOptional(type)) {
            ASTMCTypeArgument ref = getReferenceTypeFromOptional(type);
            return printType(ref);
        }
        return CollectionTypesPrinter.printType(type);
    }

    public static boolean isNullable(ASTMCType type) {
        return !isPrimitive(type);
    }

  public static String printType(ASTMCTypeArgument type) {
    if (type instanceof ASTMCWildcardTypeArgument) {
      return FullGenericTypesPrinter.printType((ASTMCWildcardTypeArgument) type);
    }
    return printType((ASTMCType) type);
  }

    public static String printSimpleRefType(ASTMCType type) {
        if (isOptional(type)) {
            return printType(getSimpleReferenceTypeFromOptional(type));
        }
        return CollectionTypesPrinter.printType(type);
    }


    public static SymTypeExpression mcType2TypeExpression(ASTMCBasicTypesNode type) {
        SynthesizeSymTypeFromMCSimpleGenericTypes visitor = new SynthesizeSymTypeFromMCSimpleGenericTypes();
        visitor.init();
        type.accept(visitor);
        return visitor.getResult().get();
    }
}
