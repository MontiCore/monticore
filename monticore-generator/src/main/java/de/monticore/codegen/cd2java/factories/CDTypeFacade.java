package de.monticore.codegen.cd2java.factories;

import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.MCCollectionTypesMill;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class CDTypeFacade {

  private static final String PACKAGE_SEPARATOR = "\\.";

  private static CDTypeFacade cdTypeFacade;

  private final CD4AnalysisParser parser;

  private CDTypeFacade() {
    this.parser = new CD4AnalysisParser();
  }

  public static CDTypeFacade getInstance() {
    if (cdTypeFacade == null) {
      cdTypeFacade = new CDTypeFacade();
    }
    return cdTypeFacade;
  }

  public ASTMCType createTypeByDefinition(final String typeSignature) {
    Optional<ASTMCType> type;
    try {
      type = parser.parseMCType(new StringReader(typeSignature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature, e);
    }

    if (!type.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature);
    }

    return type.get();
  }

  public ASTMCQualifiedType createReferenceTypeByDefinition(final String typeSignature) {
    Optional<ASTMCQualifiedType> type;
    try {
      type = parser.parseMCQualifiedType(new StringReader(typeSignature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature, e);
    }

    if (!type.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature);
    }

    return type.get();
  }

  public ASTMCQualifiedType createSimpleReferenceType(final Class<?> clazz) {
    return createSimpleReferenceType(clazz.getSimpleName());
  }

  public ASTMCQualifiedType createSimpleReferenceType(final String name) {
    return MCCollectionTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(Arrays.asList(name.split(PACKAGE_SEPARATOR)))
        .build();
  }

  public ASTMCObjectType createOptionalTypeOf(final Class<?> clazz) {
    return createOptionalTypeOf(clazz.getSimpleName());
  }

  public ASTMCObjectType createOptionalTypeOf(final String name) {
    return createSimpleReferenceType(Optional.class, name);
  }

  public ASTMCObjectType createOptionalTypeOf(final ASTMCType type) {
    return createSimpleReferenceType(Optional.class, MCCollectionTypesHelper.printType(type));
  }

  public ASTMCObjectType createListTypeOf(final Class<?> clazz) {
    return createListTypeOf(clazz.getSimpleName());
  }

  public ASTMCObjectType createListTypeOf(final String name) {
    return createSimpleReferenceType(List.class, name);
  }

  public ASTMCObjectType createListTypeOf(final ASTMCType type) {
    return createSimpleReferenceType(List.class, MCCollectionTypesHelper.printType(type));
  }

  public ASTMCObjectType createCollectionTypeOf(final Class<?> clazz) {
    return createCollectionTypeOf(clazz.getSimpleName());
  }

  public ASTMCObjectType createCollectionTypeOf(final String name) {
    return createSimpleReferenceType(Collection.class, name);
  }

  public ASTMCObjectType createCollectionTypeOf(final ASTMCType type) {
    return createSimpleReferenceType(Collection.class, MCCollectionTypesHelper.printType(type));
  }

  public ASTMCObjectType createMapTypeOf(final String firstType, final String secondType) {
    return createSimpleReferenceType(Map.class, firstType, secondType);
  }

  private ASTMCObjectType createSimpleReferenceType(final Class<?> clazz, final String... names) {
    CDTypeBuilder cdTypeBuilder = CDTypeBuilder.newTypeBuilder().simpleName(clazz);
    for (String name : names)
      cdTypeBuilder.qualifiedGenericType(name.split(PACKAGE_SEPARATOR));
    return cdTypeBuilder.build();
  }

  public ASTMCVoidType createVoidType() {
    return MCBasicTypesMill.mCVoidTypeBuilder()
        .build();
  }

  public ASTMCType createBooleanType() {
    return createPrimitiveType(ASTConstantsMCBasicTypes.BOOLEAN);
  }

  public boolean isBooleanType(ASTMCType type) {
    return type.deepEquals(createBooleanType());
  }

  public ASTMCType createIntType() {
    return createPrimitiveType(ASTConstantsMCBasicTypes.INT);
  }

  private ASTMCType createPrimitiveType(int constantsType) {
    return MCBasicTypesMill.mCPrimitiveTypeBuilder()
        .setPrimitive(constantsType)
        .build();
  }
}
