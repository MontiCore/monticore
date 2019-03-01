package de.monticore.codegen.cd2java.factories;

import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.types._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class CDTypeFactory {

  private static final String PACKAGE_SEPARATOR = "\\.";

  private static CDTypeFactory cdTypeFactory;

  private final CD4AnalysisParser parser;

  private CDTypeFactory() {
    this.parser = new CD4AnalysisParser();
  }

  public static CDTypeFactory getInstance() {
    if (cdTypeFactory == null) {
      cdTypeFactory = new CDTypeFactory();
    }
    return cdTypeFactory;
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

  public ASTSimpleReferenceType createListTypeOf(final Class<?> clazz) {
    return createListTypeOf(clazz.getSimpleName());
  }

  public ASTSimpleReferenceType createListTypeOf(final String name) {
    return createSimpleReferenceType(List.class, name);
  }

  private ASTSimpleReferenceType createSimpleReferenceType(final Class<?> clazz, final String name) {
    return CDTypeBuilder.newTypeBuilder()
        .simpleName(clazz)
        .qualifiedGenericType(name.split(PACKAGE_SEPARATOR))
        .build();
  }

  public ASTComplexArrayType createArrayType(final Class<?> clazz, int dimension) {
    return createArrayType(clazz.getSimpleName(), dimension);
  }

  public ASTComplexArrayType createArrayType(final String name, int dimension) {
    return createArrayType(this.createSimpleReferenceType(name), dimension);
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

  public ASTType createBooleanType() {
    return createPrimitiveType(ASTConstantsTypes.BOOLEAN);
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
