package de.monticore.codegen.cd2java.factories;

import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;

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

  public ASTType createTypeByDefinition(final String typeSignature) {
    Optional<ASTType> type;
    try {
      type = parser.parseType(new StringReader(typeSignature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature, e);
    }

    if (!type.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature);
    }

    return type.get();
  }

  public ASTReferenceType createReferenceTypeByDefinition(final String typeSignature) {
    Optional<ASTReferenceType> type;
    try {
      type = parser.parseReferenceType(new StringReader(typeSignature));
    } catch (IOException e) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature, e);
    }

    if (!type.isPresent()) {
      throw new CDFactoryException(CDFactoryErrorCode.COULD_NOT_CREATE_TYPE, typeSignature);
    }

    return type.get();
  }

  public ASTSimpleReferenceType createSimpleReferenceType(final Class<?> clazz) {
    return createSimpleReferenceType(clazz.getSimpleName());
  }

  public ASTSimpleReferenceType createSimpleReferenceType(final String name) {
    return TypesMill.simpleReferenceTypeBuilder()
        .setNameList(Arrays.asList(name.split(PACKAGE_SEPARATOR)))
        .build();
  }

  public ASTSimpleReferenceType createOptionalTypeOf(final Class<?> clazz) {
    return createOptionalTypeOf(clazz.getSimpleName());
  }

  public ASTSimpleReferenceType createOptionalTypeOf(final String name) {
    return createSimpleReferenceType(Optional.class, name);
  }

  public ASTSimpleReferenceType createOptionalTypeOf(final ASTType type) {
    return createSimpleReferenceType(Optional.class, TypesHelper.printType(type));
  }

  public ASTSimpleReferenceType createListTypeOf(final Class<?> clazz) {
    return createListTypeOf(clazz.getSimpleName());
  }

  public ASTSimpleReferenceType createListTypeOf(final String name) {
    return createSimpleReferenceType(List.class, name);
  }

  public ASTSimpleReferenceType createListTypeOf(final ASTType type) {
    return createSimpleReferenceType(List.class, TypesHelper.printType(type));
  }

  public ASTSimpleReferenceType createCollectionTypeOf(final Class<?> clazz) {
    return createCollectionTypeOf(clazz.getSimpleName());
  }

  public ASTSimpleReferenceType createCollectionTypeOf(final String name) {
    return createSimpleReferenceType(Collection.class, name);
  }

  public ASTSimpleReferenceType createCollectionTypeOf(final ASTType type) {
    return createSimpleReferenceType(Collection.class, TypesHelper.printType(type));
  }

  public ASTSimpleReferenceType createMapTypeOf(final String firstType, final String secondType) {
    return createSimpleReferenceType(Map.class, firstType, secondType);
  }

  private ASTSimpleReferenceType createSimpleReferenceType(final Class<?> clazz, final String... names) {
    CDTypeBuilder cdTypeBuilder = CDTypeBuilder.newTypeBuilder().simpleName(clazz);
    for (String name : names)
      cdTypeBuilder.qualifiedGenericType(name.split(PACKAGE_SEPARATOR));
    return cdTypeBuilder.build();
  }

  public ASTComplexReferenceType createComplexReferenceType(final String name) {
    return TypesMill.complexReferenceTypeBuilder()
        .setSimpleReferenceTypeList(Arrays.asList(createSimpleReferenceType(name)))
        .build();
  }

  public ASTComplexArrayType createArrayType(final Class<?> clazz, int dimension) {
    return createArrayType(clazz.getSimpleName(), dimension);
  }

  public ASTComplexArrayType createArrayType(final String name, int dimension) {
    return createArrayType(this.createComplexReferenceType(name), dimension);
  }

  private ASTComplexArrayType createArrayType(final ASTType type, int dimension) {
    return TypesMill.complexArrayTypeBuilder()
        .setComponentType(type)
        .setDimensions(dimension)
        .build();
  }

  public ASTReturnType createVoidType() {
    return TypesMill.voidTypeBuilder()
        .build();
  }

  public ASTType createStringType() {
    return createSimpleReferenceType(String.class);
  }

  public ASTType createBooleanType() {
    return createPrimitiveType(ASTConstantsTypes.BOOLEAN);
  }

  public boolean isBooleanType(ASTType type) {
    return type.deepEquals(createBooleanType());
  }

  public ASTType createIntType() {
    return createPrimitiveType(ASTConstantsTypes.INT);
  }

  private ASTType createPrimitiveType(int constantsType) {
    return TypesMill.primitiveTypeBuilder()
        .setPrimitive(constantsType)
        .build();
  }
}
