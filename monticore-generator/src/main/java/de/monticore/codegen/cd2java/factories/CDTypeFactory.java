package de.monticore.codegen.cd2java.factories;

import de.monticore.codegen.cd2java.factories.exception.CDFactoryErrorCode;
import de.monticore.codegen.cd2java.factories.exception.CDFactoryException;
import de.monticore.types.types._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

public class CDTypeFactory {

  public static final ASTReturnType VOID_TYPE = CDTypeFactory.getInstance().createVoidType();

  public static final ASTType BOOLEAN_TYPE = CDTypeFactory.getInstance().createBooleanType();

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
    return TypesMill.simpleReferenceTypeBuilder()
        .addName(clazz.getCanonicalName())
        .build();
  }

  public ASTSimpleReferenceType createSimpleReferenceType(final String name) {
    return TypesMill.simpleReferenceTypeBuilder()
        .addName(name)
        .build();
  }

  public ASTReturnType createVoidType() {
    return TypesMill.voidTypeBuilder()
        .build();
  }

  public ASTType createBooleanType() {
    return TypesMill.primitiveTypeBuilder()
        .setPrimitive(ASTConstantsTypes.BOOLEAN)
        .build();
  }
}
