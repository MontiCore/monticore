package de.monticore.types;

import com.google.common.base.Preconditions;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardType;
import de.se_rwth.commons.logging.Log;

public class MCFullGenericTypesHelper extends MCSimpleGenericTypesHelper {

  public static String getReferenceNameFromOptional(ASTMCType type) {
    // Printer instead of last implementation
    Preconditions.checkArgument(isOptional(type));
    // TODO: improve
    ASTMCTypeArgument reference = ((ASTMCGenericType) type).getMCTypeArgumentList().get(0);
    if (reference instanceof ASTMCWildcardType
        && ((ASTMCWildcardType) reference).isPresentUpperBound()) {
      ASTMCType typeRef = ((ASTMCWildcardType)reference).getUpperBound();
      return FullGenericTypesPrinter.printType(typeRef);
    }
    return FullGenericTypesPrinter.printType(reference);
  }

  public static String getQualifiedReferenceNameFromOptional(ASTMCType type) {
    Preconditions.checkArgument(isOptional(type));
    // TODO: improve
    ASTMCTypeArgument reference = ((ASTMCGenericType) type).getMCTypeArgumentList().get(0);
    if (reference instanceof ASTMCWildcardType
        && ((ASTMCWildcardType) reference).isPresentUpperBound()) {
      ASTMCType typeRef = ((ASTMCWildcardType)reference).getUpperBound();
      return FullGenericTypesPrinter.printType(typeRef);
    }
    return FullGenericTypesPrinter.printType(reference);
  }

  public static int getArrayDimensionIfArrayOrZero(ASTMCType astType) {
    return (astType instanceof ASTMCArrayType)? ((ASTMCArrayType) astType).getDimensions() : 0;
  }
//
  public static String printType(ASTMCTypeArgument type) {
    return FullGenericTypesPrinter.printType(type);
  }

  public static ASTMCGenericType getSimpleReferenceTypeFromOptional(ASTMCType type) {
    Preconditions.checkArgument(isOptional(type));
    ASTMCTypeArgument refType = getReferenceTypeFromOptional(type);
    // TODO: improve
//    if (refType instanceof ASTMCWildcardType && ((ASTMCWildcardType) refType).isPresentUpperBound()) {
//      ASTMCType typeRef = ((ASTMCWildcardType) refType).getUpperBound();
//      Preconditions.checkState(typeRef instanceof ASTMCGenericType);
//      return (ASTMCGenericType) typeRef;
//    }
//    // TODO: improve
//    if(refType instanceof ASTMCBasicTypeArgument){
//      ASTMCBasicTypeArgument basicRef = (ASTMCBasicTypeArgument) refType;
//      if(basicRef.getMCQualifiedType() instanceof ASTMCGenericType){
//        return (ASTMCGenericType) basicRef.getMCQualifiedType();
//      }
//    }
//    if(refType instanceof ASTMCCustomTypeArgument) {
//      ASTMCCustomTypeArgument customRef = (ASTMCCustomTypeArgument) refType;
//      if (customRef.getMCType() instanceof ASTMCGenericType) {
//        return (ASTMCGenericType) customRef.getMCType();
//      }
//    }
    if(refType.getMCTypeOpt().isPresent()){
      if(refType.getMCTypeOpt().get() instanceof ASTMCGenericType) {
        return (ASTMCGenericType) refType.getMCTypeOpt().get();
      }
    }else{
      ASTMCType typeRef = ((ASTMCWildcardType) refType).getUpperBound();
      Preconditions.checkState(typeRef instanceof ASTMCGenericType);
      return (ASTMCGenericType) typeRef;
    }
    Log.error("Something went wrong");
    return null;
  }

  public static ASTMCGenericType getGenericTypeFromOptional(ASTMCType type) {
    Preconditions.checkArgument(isOptional(type));
    ASTMCTypeArgument refType = getReferenceTypeFromOptional(type);
//    if(refType instanceof ASTMCBasicTypeArgument){
//      ASTMCBasicTypeArgument basicTypeArgument = (ASTMCBasicTypeArgument) refType;
//      Preconditions.checkState(basicTypeArgument.getMCQualifiedType().getMCQualifiedName() instanceof ASTMCGenericType);
//      return (ASTMCGenericType) (basicTypeArgument.getMCQualifiedType().getMCQualifiedName());
//    }else if(refType instanceof ASTMCPrimitiveTypeArgument){
//      ASTMCPrimitiveTypeArgument primitiveTypeArgument = (ASTMCPrimitiveTypeArgument) refType;
//      Preconditions.checkState(primitiveTypeArgument.getMCPrimitiveType() instanceof ASTMCGenericType);
//      return (ASTMCGenericType) primitiveTypeArgument.getMCPrimitiveType();
//    }else if(refType instanceof ASTMCCustomTypeArgument){
//      ASTMCCustomTypeArgument astmcCustomTypeArgument = (ASTMCCustomTypeArgument) refType;
//      Preconditions.checkState(astmcCustomTypeArgument.getMCType() instanceof ASTMCGenericType);
//      return (ASTMCGenericType) astmcCustomTypeArgument.getMCType();
//    }
    Preconditions.checkState(refType.getMCTypeOpt().get() instanceof ASTMCGenericType);
    return (ASTMCGenericType) (refType.getMCTypeOpt().get());
  }
}
